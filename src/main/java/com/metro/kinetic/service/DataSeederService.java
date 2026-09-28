package com.metro.kinetic.service;

import com.metro.kinetic.model.*;
import com.metro.kinetic.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Random;

@Component
public class DataSeederService implements CommandLineRunner {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private AdminUserRepository adminRepo;

    @Autowired
    private PowerGridCustomerRepository customerRepo;

    @Autowired
    private TileVendorRepository vendorRepo;

    @Autowired
    private KineticTileRepository tileRepo;

    @Autowired
    private StationSectorRepository sectorRepo;

    @Autowired
    private TelemetryRepository telemetryRepo;

    @Autowired
    private BudgetRepository budgetRepo;

    @Autowired
    private ChartOfAccountRepository accountRepo;

    @Autowired
    private AuthService authService;

    @Autowired
    private EnergySalesService energySalesService;

    @Autowired
    private VendorBillService vendorBillService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AccountingService accountingService;

    @Override
    public void run(String... args) throws Exception {
        // Seed only if users table is empty
        if (adminRepo.count() > 0) {
            return;
        }

        System.out.println(">>> SEEDING KINETIC ENERGY SYSTEM DEMO DATA INTO MYSQL...");

        // 1. Chart of Accounts
        accountRepo.save(new ChartOfAccount("Bank", "Asset", 500000.0));
        accountRepo.save(new ChartOfAccount("Customer Receivable", "Asset", 0.0));
        accountRepo.save(new ChartOfAccount("Vendor Payable", "Liability", 0.0));
        accountRepo.save(new ChartOfAccount("Transit Capital Fund", "Equity", 500000.0));
        accountRepo.save(new ChartOfAccount("Harvested Power Sales Revenue", "Revenue", 0.0));
        accountRepo.save(new ChartOfAccount("Maintenance Expense", "Expense", 0.0));
        accountRepo.save(new ChartOfAccount("Inverter Expenses", "Expense", 0.0));

        // Initial Capital Endowment Journal Entry (₹5,00,000)
        accountingService.recordTransaction(
                "CAPITAL_ENDOWMENT", "INITIAL_FUND", 1L,
                "Bank", "Transit Capital Fund", 500000.0, LocalDate.now().minusDays(30)
        );

        // 2. Admins and Users (BCrypt Encrypted)
        authService.registerAdmin("Metro Transit Admin", "admin@metro.in", "admin123");
        authService.registerUser("Subway Station Operator", "user@metro.in", "user123");

        // 3. Station Sectors
        StationSector sec1 = sectorRepo.save(new StationSector("Central Metro Station", "North Turnstile Gates", "Entrance Mezzanine Fare Barriers", 24));
        StationSector sec2 = sectorRepo.save(new StationSector("Rajiv Chowk Interchange", "Platform 1 & 2 Transfer Corridor", "Main Passenger Interconnecting Walkway", 18));
        StationSector sec3 = sectorRepo.save(new StationSector("City Center Terminus", "Concourse Exit Apron", "Escalator Discharge Zone", 16));
        StationSector sec4 = sectorRepo.save(new StationSector("Airport Express Hub", "Baggage Claim Hallway", "Terminal 3 Connector Array", 12));

        // 4. Kinetic Floor Tiles
        tileRepo.save(new KineticTile("KT-CENTRAL-01", "Piezoelectric Dual-Stack", 45000.0, 10, "Active"));
        tileRepo.save(new KineticTile("KT-CENTRAL-02", "Electromagnetic Micro-Dynamo", 58000.0, 8, "Active"));
        tileRepo.save(new KineticTile("KT-RAJIV-01", "Hybrid Piezo-Electromagnetic", 65000.0, 12, "Active"));
        tileRepo.save(new KineticTile("KT-EXPRESS-01", "Low-Profile Spring Damper", 40000.0, 6, "Maintenance"));

        // 5. Power Grid Customers
        PowerGridCustomer c1 = customerRepo.save(new PowerGridCustomer("Northern Regional Power Grid Ltd", "+91-11-2345-6789", "grid@nrpc.gov.in", "Substation Intertie Feeder 4, New Delhi"));
        PowerGridCustomer c2 = customerRepo.save(new PowerGridCustomer("Metro Rail Auxiliary Traction Discom", "+91-11-2987-6543", "discom@metrorail.in", "Traction Substation 12, Connaught Place"));
        PowerGridCustomer c3 = customerRepo.save(new PowerGridCustomer("Delhi Green Urban Microgrid Corp", "+91-11-2456-7890", "power@greenmicrogrid.in", "Commercial Concourse Feeder"));

        // 6. Tile Vendors
        TileVendor v1 = vendorRepo.save(new TileVendor("PaveStep Energy Technologies India", "+91-98765-43210", "support@pavestep.in", "Okhla Industrial Area Phase III, New Delhi"));
        TileVendor v2 = vendorRepo.save(new TileVendor("Bharat Kinetic Systems & Spares", "+91-98123-45678", "orders@bharatkinetic.in", "Peenya Industrial Area, Bengaluru"));
        TileVendor v3 = vendorRepo.save(new TileVendor("MetroRail Electro-Mech Solutions", "+91-98901-23456", "services@metrorailmech.in", "Andheri East, Mumbai"));

        // 7. Budgets
        budgetRepo.save(new Budget(sec1.getId(), 2026, 180000.0, 45000.0, 142000.0, 38000.0));
        budgetRepo.save(new Budget(sec2.getId(), 2026, 150000.0, 40000.0, 126000.0, 32000.0));
        budgetRepo.save(new Budget(sec3.getId(), 2026, 120000.0, 30000.0, 98000.0, 24000.0));
        budgetRepo.save(new Budget(sec4.getId(), 2026, 100000.0, 25000.0, 85000.0, 21000.0));

        // 8. Historical Telemetry (Past 7 Days)
        Random random = new Random(42);
        StationSector[] allSectors = {sec1, sec2, sec3, sec4};
        for (int i = 7; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            for (StationSector sector : allSectors) {
                long steps = 8000L + random.nextInt(12000);
                double kwh = Math.round(steps * 0.00026 * 1000.0) / 1000.0;
                telemetryRepo.save(new Telemetry(sector.getId(), date, steps, kwh));
            }
        }

        // 9. Initial Sales Order & Invoice with Double-entry
        // Sale 1: Paid (1,000 kWh @ ₹8.00 = ₹8,000)
        CustomerInvoice inv1 = energySalesService.createEnergySale(c1.getId(), LocalDate.now().minusDays(5), 1000.0, 8.0);
        paymentService.recordCustomerPayment(inv1.getId(), LocalDate.now().minusDays(4), "Bank Transfer");

        // Sale 2: Paid (1,500 kWh @ ₹8.50 = ₹12,750)
        CustomerInvoice inv2 = energySalesService.createEnergySale(c2.getId(), LocalDate.now().minusDays(3), 1500.0, 8.5);
        paymentService.recordCustomerPayment(inv2.getId(), LocalDate.now().minusDays(2), "NEFT");

        // Sale 3: Unpaid (800 kWh @ ₹9.00 = ₹7,200)
        energySalesService.createEnergySale(c3.getId(), LocalDate.now().minusDays(1), 800.0, 9.0);

        // 10. Initial Vendor Bills with Double-entry
        // Bill 1: Paid (2x Replacement Piezoelectric Sensor Modules = ₹14,000)
        VendorBill bill1 = vendorBillService.createVendorBill(v1.getId(), LocalDate.now().minusDays(6), "Replacement Piezoelectric Sensor Modules", 2, 14000.0);
        paymentService.recordVendorPayment(bill1.getId(), LocalDate.now().minusDays(5), "Bank Transfer");

        // Bill 2: Unpaid (1x Inverter Maintenance & Calibration = ₹8,500)
        vendorBillService.createVendorBill(v3.getId(), LocalDate.now().minusDays(2), "Inverter Maintenance & Grid Filter Calibration", 1, 8500.0);

        System.out.println(">>> SEEDING COMPLETE! ALL 14 TABLES INITIALIZED SUCCESSFULLY IN MYSQL.");
    }
}

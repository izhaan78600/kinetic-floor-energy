package com.metro.kinetic.controller;

import com.metro.kinetic.model.ChartOfAccount;
import com.metro.kinetic.model.JournalEntry;
import com.metro.kinetic.model.KineticTile;
import com.metro.kinetic.model.PowerGridCustomer;
import com.metro.kinetic.model.TileVendor;
import com.metro.kinetic.repository.KineticTileRepository;
import com.metro.kinetic.repository.PowerGridCustomerRepository;
import com.metro.kinetic.repository.TileVendorRepository;
import com.metro.kinetic.service.AccountingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class MasterDataController {

    @Autowired
    private PowerGridCustomerRepository customerRepo;

    @Autowired
    private TileVendorRepository vendorRepo;

    @Autowired
    private KineticTileRepository tileRepo;

    @Autowired
    private AccountingService accountingService;

    // ==========================================
    // POWER GRID CUSTOMERS
    // ==========================================
    @GetMapping("/customers")
    public ResponseEntity<List<PowerGridCustomer>> getCustomers() {
        return ResponseEntity.ok(customerRepo.findAll());
    }

    @PostMapping("/customers")
    public ResponseEntity<?> addCustomer(@RequestBody PowerGridCustomer customer) {
        if (customer.getCustomerName() == null || customer.getCustomerName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Customer name is required"));
        }
        return ResponseEntity.ok(customerRepo.save(customer));
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        customerRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Customer removed successfully"));
    }

    // ==========================================
    // TILE VENDORS
    // ==========================================
    @GetMapping("/vendors")
    public ResponseEntity<List<TileVendor>> getVendors() {
        return ResponseEntity.ok(vendorRepo.findAll());
    }

    @PostMapping("/vendors")
    public ResponseEntity<?> addVendor(@RequestBody TileVendor vendor) {
        if (vendor.getVendorName() == null || vendor.getVendorName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Vendor name is required"));
        }
        return ResponseEntity.ok(vendorRepo.save(vendor));
    }

    @DeleteMapping("/vendors/{id}")
    public ResponseEntity<?> deleteVendor(@PathVariable Long id) {
        vendorRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Vendor removed successfully"));
    }

    // ==========================================
    // KINETIC FLOOR TILES
    // ==========================================
    @GetMapping("/tiles")
    public ResponseEntity<List<KineticTile>> getTiles() {
        return ResponseEntity.ok(tileRepo.findAll());
    }

    @PostMapping("/tiles")
    public ResponseEntity<?> addTile(@RequestBody KineticTile tile) {
        if (tile.getTileName() == null || tile.getTileName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Tile name is required"));
        }
        return ResponseEntity.ok(tileRepo.save(tile));
    }

    @DeleteMapping("/tiles/{id}")
    public ResponseEntity<?> deleteTile(@PathVariable Long id) {
        tileRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Tile removed successfully"));
    }

    // ==========================================
    // CHART OF ACCOUNTS & JOURNAL ENTRIES
    // ==========================================
    @GetMapping("/accounts")
    public ResponseEntity<List<ChartOfAccount>> getChartOfAccounts() {
        return ResponseEntity.ok(accountingService.getAllAccounts());
    }

    @GetMapping("/journal-entries")
    public ResponseEntity<List<JournalEntry>> getJournalEntries() {
        return ResponseEntity.ok(accountingService.getAllJournalEntries());
    }
}

package com.metro.kinetic.service;

import com.metro.kinetic.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReportService {

    @Autowired
    private TelemetryRepository telemetryRepo;

    @Autowired
    private SalesOrderRepository salesOrderRepo;

    @Autowired
    private CustomerInvoiceRepository invoiceRepo;

    @Autowired
    private VendorBillRepository vendorBillRepo;

    @Autowired
    private BudgetRepository budgetRepo;

    @Autowired
    private JournalEntryRepository journalRepo;

    @Autowired
    private ChartOfAccountRepository accountRepo;

    // ==========================================
    // 1. PROFIT & LOSS REPORT
    // ==========================================
    public Map<String, Object> getProfitAndLossReport() {
        Map<String, Object> report = new LinkedHashMap<>();

        // Revenue: Harvested Power Sales Revenue
        Double salesRevenue = journalRepo.sumCreditByAccount("Harvested Power Sales Revenue");
        if (salesRevenue == null) salesRevenue = 0.0;

        // Expenses: Maintenance Expense
        Double maintExpense = journalRepo.sumDebitByAccount("Maintenance Expense");
        if (maintExpense == null) maintExpense = 0.0;

        // Expenses: Inverter Expenses
        Double inverterExpense = journalRepo.sumDebitByAccount("Inverter Expenses");
        if (inverterExpense == null) inverterExpense = 0.0;

        double totalExpenses = maintExpense + inverterExpense;
        double netProfit = salesRevenue - totalExpenses;
        double marginPct = salesRevenue > 0 ? Math.round((netProfit / salesRevenue) * 1000.0) / 10.0 : 0.0;

        report.put("energySalesRevenueInr", Math.round(salesRevenue * 100.0) / 100.0);
        report.put("maintenanceExpensesInr", Math.round(maintExpense * 100.0) / 100.0);
        report.put("inverterExpensesInr", Math.round(inverterExpense * 100.0) / 100.0);
        report.put("totalExpensesInr", Math.round(totalExpenses * 100.0) / 100.0);
        report.put("netProfitInr", Math.round(netProfit * 100.0) / 100.0);
        report.put("profitMarginPercent", marginPct);

        return report;
    }

    // ==========================================
    // 2. BUDGET REPORT
    // ==========================================
    public Map<String, Object> getBudgetReport() {
        Map<String, Object> report = new LinkedHashMap<>();
        List<Map<String, Object>> sectorBudgets = new ArrayList<>();

        double sumPlannedRev = 0.0;
        double sumActualRev = 0.0;
        double sumPlannedExp = 0.0;
        double sumActualExp = 0.0;

        var budgets = budgetRepo.findAllByOrderByBudgetYearDesc();
        for (var b : budgets) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", b.getId());
            item.put("sectorName", b.getSector() != null ? b.getSector().getSectorName() : "Sector " + b.getSectorId());
            item.put("stationName", b.getSector() != null ? b.getSector().getStationName() : "Station");
            item.put("budgetYear", b.getBudgetYear());
            item.put("plannedRevenueInr", b.getPlannedRevenueInr());
            item.put("actualRevenueInr", b.getActualRevenueInr());
            item.put("plannedExpensesInr", b.getPlannedMaintenanceInr());
            item.put("actualExpensesInr", b.getActualMaintenanceInr());

            double revVar = b.getActualRevenueInr() - b.getPlannedRevenueInr();
            double expVar = b.getPlannedMaintenanceInr() - b.getActualMaintenanceInr();
            item.put("revenueVarianceInr", Math.round(revVar * 100.0) / 100.0);
            item.put("expenseVarianceInr", Math.round(expVar * 100.0) / 100.0);

            sumPlannedRev += b.getPlannedRevenueInr();
            sumActualRev += b.getActualRevenueInr();
            sumPlannedExp += b.getPlannedMaintenanceInr();
            sumActualExp += b.getActualMaintenanceInr();

            sectorBudgets.add(item);
        }

        report.put("totalPlannedRevenueInr", Math.round(sumPlannedRev * 100.0) / 100.0);
        report.put("totalActualRevenueInr", Math.round(sumActualRev * 100.0) / 100.0);
        report.put("totalPlannedExpensesInr", Math.round(sumPlannedExp * 100.0) / 100.0);
        report.put("totalActualExpensesInr", Math.round(sumActualExp * 100.0) / 100.0);
        report.put("netRevenueVarianceInr", Math.round((sumActualRev - sumPlannedRev) * 100.0) / 100.0);
        report.put("netExpenseVarianceInr", Math.round((sumPlannedExp - sumActualExp) * 100.0) / 100.0);
        report.put("sectors", sectorBudgets);

        return report;
    }

    // ==========================================
    // 3. ENERGY REPORT
    // ==========================================
    public Map<String, Object> getEnergyReport() {
        Map<String, Object> report = new LinkedHashMap<>();

        Long totalSteps = telemetryRepo.sumTotalStepCount();
        Double totalKwhGenerated = telemetryRepo.sumTotalEnergyKwh();
        Double totalKwhSold = salesOrderRepo.sumTotalEnergySoldKwh();

        if (totalSteps == null) totalSteps = 0L;
        if (totalKwhGenerated == null) totalKwhGenerated = 0.0;
        if (totalKwhSold == null) totalKwhSold = 0.0;

        double availableReserve = Math.max(0.0, totalKwhGenerated - totalKwhSold);

        report.put("totalSteps", totalSteps);
        report.put("totalKwhGenerated", Math.round(totalKwhGenerated * 1000.0) / 1000.0);
        report.put("totalKwhSold", Math.round(totalKwhSold * 1000.0) / 1000.0);
        report.put("availableReserveKwh", Math.round(availableReserve * 1000.0) / 1000.0);

        // Daily breakdown
        List<Object[]> daily = telemetryRepo.findDailyAggregated();
        List<Map<String, Object>> dailyList = new ArrayList<>();
        for (Object[] row : daily) {
            Map<String, Object> d = new HashMap<>();
            d.put("date", row[0].toString());
            d.put("steps", ((Number) row[1]).longValue());
            d.put("kwh", Math.round(((Number) row[2]).doubleValue() * 1000.0) / 1000.0);
            dailyList.add(d);
        }
        report.put("dailyBreakdown", dailyList);

        return report;
    }

    // ==========================================
    // 4. INVOICE REPORT
    // ==========================================
    public Map<String, Object> getInvoiceReport() {
        Map<String, Object> report = new LinkedHashMap<>();
        var invoices = invoiceRepo.findAllByOrderByIdDesc();

        double totalInvoiced = 0.0;
        double totalPaid = 0.0;
        double totalUnpaid = 0.0;

        for (var inv : invoices) {
            totalInvoiced += inv.getAmountInr();
            if ("Paid".equalsIgnoreCase(inv.getStatus())) {
                totalPaid += inv.getAmountInr();
            } else {
                totalUnpaid += inv.getAmountInr();
            }
        }

        report.put("totalInvoicesCount", invoices.size());
        report.put("totalInvoicedInr", Math.round(totalInvoiced * 100.0) / 100.0);
        report.put("totalPaidInr", Math.round(totalPaid * 100.0) / 100.0);
        report.put("totalUnpaidInr", Math.round(totalUnpaid * 100.0) / 100.0);
        report.put("invoices", invoices);

        return report;
    }

    // ==========================================
    // 5. VENDOR BILL REPORT
    // ==========================================
    public Map<String, Object> getVendorBillReport() {
        Map<String, Object> report = new LinkedHashMap<>();
        var bills = vendorBillRepo.findAllByOrderByIdDesc();

        double totalBilled = 0.0;
        double totalPaid = 0.0;
        double totalUnpaid = 0.0;

        for (var bill : bills) {
            totalBilled += bill.getAmountInr();
            if ("Paid".equalsIgnoreCase(bill.getStatus())) {
                totalPaid += bill.getAmountInr();
            } else {
                totalUnpaid += bill.getAmountInr();
            }
        }

        report.put("totalBillsCount", bills.size());
        report.put("totalBilledInr", Math.round(totalBilled * 100.0) / 100.0);
        report.put("totalPaidInr", Math.round(totalPaid * 100.0) / 100.0);
        report.put("totalUnpaidInr", Math.round(totalUnpaid * 100.0) / 100.0);
        report.put("bills", bills);

        return report;
    }

    // ==========================================
    // 6. DASHBOARD AGGREGATED STATS
    // ==========================================
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new LinkedHashMap<>();

        Long totalSteps = telemetryRepo.sumTotalStepCount();
        Double totalEnergyGen = telemetryRepo.sumTotalEnergyKwh();
        Double totalEnergySold = salesOrderRepo.sumTotalEnergySoldKwh();
        Double totalRevenue = journalRepo.sumCreditByAccount("Harvested Power Sales Revenue");
        Double maintExp = journalRepo.sumDebitByAccount("Maintenance Expense");
        Double inverterExp = journalRepo.sumDebitByAccount("Inverter Expenses");

        if (totalSteps == null) totalSteps = 0L;
        if (totalEnergyGen == null) totalEnergyGen = 0.0;
        if (totalEnergySold == null) totalEnergySold = 0.0;
        if (totalRevenue == null) totalRevenue = 0.0;
        if (maintExp == null) maintExp = 0.0;
        if (inverterExp == null) inverterExp = 0.0;

        double totalExpenses = maintExp + inverterExp;
        double netProfit = totalRevenue - totalExpenses;

        // Account balances
        Double bankBal = journalRepo.sumDebitByAccount("Bank");
        Double bankCredit = journalRepo.sumCreditByAccount("Bank");
        double netBank = (bankBal != null ? bankBal : 0.0) - (bankCredit != null ? bankCredit : 0.0);

        Double recDebit = journalRepo.sumDebitByAccount("Customer Receivable");
        Double recCredit = journalRepo.sumCreditByAccount("Customer Receivable");
        double netReceivable = (recDebit != null ? recDebit : 0.0) - (recCredit != null ? recCredit : 0.0);

        Double payCredit = journalRepo.sumCreditByAccount("Vendor Payable");
        Double payDebit = journalRepo.sumDebitByAccount("Vendor Payable");
        double netPayable = (payCredit != null ? payCredit : 0.0) - (payDebit != null ? payDebit : 0.0);

        stats.put("totalSteps", totalSteps);
        stats.put("totalEnergyGeneratedKwh", Math.round(totalEnergyGen * 100.0) / 100.0);
        stats.put("totalEnergySoldKwh", Math.round(totalEnergySold * 100.0) / 100.0);
        stats.put("totalRevenueInr", Math.round(totalRevenue * 100.0) / 100.0);
        stats.put("totalExpensesInr", Math.round(totalExpenses * 100.0) / 100.0);
        stats.put("netProfitInr", Math.round(netProfit * 100.0) / 100.0);
        stats.put("bankBalanceInr", Math.round(netBank * 100.0) / 100.0);
        stats.put("customerReceivableInr", Math.round(netReceivable * 100.0) / 100.0);
        stats.put("vendorPayableInr", Math.round(netPayable * 100.0) / 100.0);

        // Daily chart data
        List<Object[]> daily = telemetryRepo.findDailyAggregated();
        List<Map<String, Object>> dailyGen = new ArrayList<>();
        for (Object[] r : daily) {
            Map<String, Object> d = new HashMap<>();
            d.put("date", r[0].toString());
            d.put("steps", ((Number) r[1]).longValue());
            d.put("kwh", Math.round(((Number) r[2]).doubleValue() * 100.0) / 100.0);
            dailyGen.add(d);
        }
        stats.put("dailyGenerationChart", dailyGen);

        // Sector chart data
        List<Object[]> sectorAgg = telemetryRepo.findSectorAggregated();
        List<Map<String, Object>> sectorGen = new ArrayList<>();
        for (Object[] r : sectorAgg) {
            Map<String, Object> s = new HashMap<>();
            s.put("sectorName", r[0].toString());
            s.put("steps", ((Number) r[1]).longValue());
            s.put("kwh", Math.round(((Number) r[2]).doubleValue() * 100.0) / 100.0);
            sectorGen.add(s);
        }
        stats.put("sectorGenerationChart", sectorGen);

        // Recent Telemetry
        stats.put("recentTelemetry", telemetryRepo.findTop10ByOrderByIdDesc());

        return stats;
    }
}

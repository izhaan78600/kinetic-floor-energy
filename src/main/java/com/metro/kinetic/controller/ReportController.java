package com.metro.kinetic.controller;

import com.metro.kinetic.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/reports/profit-loss")
    public ResponseEntity<Map<String, Object>> getProfitLossReport() {
        return ResponseEntity.ok(reportService.getProfitAndLossReport());
    }

    @GetMapping("/reports/budget")
    public ResponseEntity<Map<String, Object>> getBudgetReport() {
        return ResponseEntity.ok(reportService.getBudgetReport());
    }

    @GetMapping("/reports/energy")
    public ResponseEntity<Map<String, Object>> getEnergyReport() {
        return ResponseEntity.ok(reportService.getEnergyReport());
    }

    @GetMapping("/reports/invoices")
    public ResponseEntity<Map<String, Object>> getInvoiceReport() {
        return ResponseEntity.ok(reportService.getInvoiceReport());
    }

    @GetMapping("/reports/vendor-bills")
    public ResponseEntity<Map<String, Object>> getVendorBillReport() {
        return ResponseEntity.ok(reportService.getVendorBillReport());
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        return ResponseEntity.ok(reportService.getDashboardStats());
    }
}

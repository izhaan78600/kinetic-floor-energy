package com.metro.kinetic.service;

import com.metro.kinetic.model.Budget;
import com.metro.kinetic.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepo;

    public Budget saveBudget(Budget budget) {
        return budgetRepo.save(budget);
    }

    public List<Budget> getAllBudgets() {
        return budgetRepo.findAllByOrderByBudgetYearDesc();
    }

    public List<Map<String, Object>> getBudgetsWithVariance() {
        List<Budget> budgets = budgetRepo.findAllByOrderByBudgetYearDesc();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Budget b : budgets) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", b.getId());
            map.put("sectorId", b.getSectorId());
            map.put("sectorName", b.getSector() != null ? b.getSector().getSectorName() : "Sector #" + b.getSectorId());
            map.put("stationName", b.getSector() != null ? b.getSector().getStationName() : "Station");
            map.put("budgetYear", b.getBudgetYear());
            map.put("plannedRevenueInr", b.getPlannedRevenueInr());
            map.put("plannedMaintenanceInr", b.getPlannedMaintenanceInr());
            map.put("actualRevenueInr", b.getActualRevenueInr());
            map.put("actualMaintenanceInr", b.getActualMaintenanceInr());

            // Variances:
            // Revenue Variance = Actual - Planned (Positive = Exceeded expectation)
            double revVariance = Math.round((b.getActualRevenueInr() - b.getPlannedRevenueInr()) * 100.0) / 100.0;
            // Maintenance Variance = Planned - Actual (Positive = Under budget / good)
            double maintVariance = Math.round((b.getPlannedMaintenanceInr() - b.getActualMaintenanceInr()) * 100.0) / 100.0;

            map.put("revenueVarianceInr", revVariance);
            map.put("maintenanceVarianceInr", maintVariance);
            result.add(map);
        }

        return result;
    }
}

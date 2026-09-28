package com.metro.kinetic.repository;

import com.metro.kinetic.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Optional<Budget> findBySectorIdAndBudgetYear(Long sectorId, Integer budgetYear);

    List<Budget> findAllByOrderByBudgetYearDesc();
}

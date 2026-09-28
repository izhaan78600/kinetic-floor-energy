package com.metro.kinetic.repository;

import com.metro.kinetic.model.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {

    @Query("SELECT COALESCE(SUM(s.energyKwh), 0.0) FROM SalesOrder s")
    Double sumTotalEnergySoldKwh();

    @Query("SELECT COALESCE(SUM(s.totalAmountInr), 0.0) FROM SalesOrder s")
    Double sumTotalSalesAmountInr();

    List<SalesOrder> findAllByOrderByIdDesc();
}

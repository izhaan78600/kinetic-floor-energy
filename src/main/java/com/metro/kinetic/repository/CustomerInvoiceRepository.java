package com.metro.kinetic.repository;

import com.metro.kinetic.model.CustomerInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerInvoiceRepository extends JpaRepository<CustomerInvoice, Long> {

    Optional<CustomerInvoice> findBySalesOrderId(Long salesOrderId);

    @Query("SELECT COALESCE(SUM(i.amountInr), 0.0) FROM CustomerInvoice i WHERE i.status = 'Paid'")
    Double sumPaidInvoiceAmountInr();

    @Query("SELECT COALESCE(SUM(i.amountInr), 0.0) FROM CustomerInvoice i")
    Double sumTotalInvoicedAmountInr();

    List<CustomerInvoice> findAllByOrderByIdDesc();
}

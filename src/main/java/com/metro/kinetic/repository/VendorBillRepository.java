package com.metro.kinetic.repository;

import com.metro.kinetic.model.VendorBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VendorBillRepository extends JpaRepository<VendorBill, Long> {

    @Query("SELECT COALESCE(SUM(v.amountInr), 0.0) FROM VendorBill v")
    Double sumTotalBillAmountInr();

    @Query("SELECT COALESCE(SUM(v.amountInr), 0.0) FROM VendorBill v WHERE v.status = 'Paid'")
    Double sumPaidBillAmountInr();

    List<VendorBill> findAllByOrderByIdDesc();
}

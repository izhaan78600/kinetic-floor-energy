package com.metro.kinetic.repository;

import com.metro.kinetic.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByPaymentTypeOrderByIdDesc(String paymentType);

    List<Payment> findAllByOrderByIdDesc();

    @Query("SELECT COALESCE(SUM(p.amountInr), 0.0) FROM Payment p WHERE p.paymentType = :paymentType AND p.status = 'Completed'")
    Double sumAmountByPaymentType(String paymentType);
}

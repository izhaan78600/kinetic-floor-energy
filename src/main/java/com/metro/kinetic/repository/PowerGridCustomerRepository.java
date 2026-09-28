package com.metro.kinetic.repository;

import com.metro.kinetic.model.PowerGridCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PowerGridCustomerRepository extends JpaRepository<PowerGridCustomer, Long> {
}

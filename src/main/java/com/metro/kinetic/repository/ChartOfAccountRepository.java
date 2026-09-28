package com.metro.kinetic.repository;

import com.metro.kinetic.model.ChartOfAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface ChartOfAccountRepository extends JpaRepository<ChartOfAccount, Long> {

    Optional<ChartOfAccount> findByAccountName(String accountName);

    List<ChartOfAccount> findAllByOrderByIdAsc();
}

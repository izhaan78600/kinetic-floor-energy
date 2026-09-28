package com.metro.kinetic.repository;

import com.metro.kinetic.model.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {

    List<JournalEntry> findAllByOrderByEntryDateDescIdDesc();

    @Query("SELECT COALESCE(SUM(j.amountInr), 0.0) FROM JournalEntry j WHERE j.debitAccount = :accountName")
    Double sumDebitByAccount(String accountName);

    @Query("SELECT COALESCE(SUM(j.amountInr), 0.0) FROM JournalEntry j WHERE j.creditAccount = :accountName")
    Double sumCreditByAccount(String accountName);

    @Query("SELECT COALESCE(SUM(j.amountInr), 0.0) FROM JournalEntry j")
    Double sumTotalJournalAmount();
}

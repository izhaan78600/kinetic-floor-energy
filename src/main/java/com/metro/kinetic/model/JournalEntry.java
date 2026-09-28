package com.metro.kinetic.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "journal_entries")
public class JournalEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "journal_type", nullable = false)
    private String journalType;

    @Column(name = "reference_type")
    private String referenceType;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "debit_account", nullable = false)
    private String debitAccount;

    @Column(name = "credit_account", nullable = false)
    private String creditAccount;

    @Column(name = "amount_inr", nullable = false)
    private Double amountInr;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    public JournalEntry() {}

    public JournalEntry(String journalType, String referenceType, Long referenceId, String debitAccount, String creditAccount, Double amountInr, LocalDate entryDate) {
        this.journalType = journalType;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.debitAccount = debitAccount;
        this.creditAccount = creditAccount;
        this.amountInr = amountInr;
        this.entryDate = entryDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getJournalType() { return journalType; }
    public void setJournalType(String journalType) { this.journalType = journalType; }

    public String getReferenceType() { return referenceType; }
    public void setReferenceType(String referenceType) { this.referenceType = referenceType; }

    public Long getReferenceId() { return referenceId; }
    public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }

    public String getDebitAccount() { return debitAccount; }
    public void setDebitAccount(String debitAccount) { this.debitAccount = debitAccount; }

    public String getCreditAccount() { return creditAccount; }
    public void setCreditAccount(String creditAccount) { this.creditAccount = creditAccount; }

    public Double getAmountInr() { return amountInr; }
    public void setAmountInr(Double amountInr) { this.amountInr = amountInr; }

    public LocalDate getEntryDate() { return entryDate; }
    public void setEntryDate(LocalDate entryDate) { this.entryDate = entryDate; }
}

package com.metro.kinetic.service;

import com.metro.kinetic.model.ChartOfAccount;
import com.metro.kinetic.model.JournalEntry;
import com.metro.kinetic.repository.ChartOfAccountRepository;
import com.metro.kinetic.repository.JournalEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AccountingService {

    @Autowired
    private ChartOfAccountRepository accountRepo;

    @Autowired
    private JournalEntryRepository journalRepo;

    @Transactional
    public JournalEntry recordTransaction(String journalType, String refType, Long refId,
                                          String debitAccountName, String creditAccountName,
                                          Double amountInr, LocalDate date) {
        if (date == null) date = LocalDate.now();

        // 1. Save Journal Entry
        JournalEntry entry = new JournalEntry(
                journalType, refType, refId, debitAccountName, creditAccountName, amountInr, date
        );
        entry = journalRepo.save(entry);

        // 2. Update Debit Account Balance
        updateAccountBalance(debitAccountName, amountInr, true);

        // 3. Update Credit Account Balance
        updateAccountBalance(creditAccountName, amountInr, false);

        return entry;
    }

    private void updateAccountBalance(String accountName, Double amount, boolean isDebit) {
        ChartOfAccount account = accountRepo.findByAccountName(accountName)
                .orElseGet(() -> {
                    String type = guessAccountType(accountName);
                    return accountRepo.save(new ChartOfAccount(accountName, type, 0.0));
                });

        Double current = account.getBalanceInr() != null ? account.getBalanceInr() : 0.0;
        String type = account.getAccountType();

        // Standard accounting rules:
        // Asset & Expense normal balance is Debit (+)
        // Liability, Equity & Revenue normal balance is Credit (+)
        if ("Asset".equalsIgnoreCase(type) || "Expense".equalsIgnoreCase(type)) {
            account.setBalanceInr(isDebit ? (current + amount) : (current - amount));
        } else {
            account.setBalanceInr(isDebit ? (current - amount) : (current + amount));
        }

        accountRepo.save(account);
    }

    private String guessAccountType(String name) {
        String lower = name.toLowerCase();
        if (lower.contains("bank") || lower.contains("receivable") || lower.contains("cash")) return "Asset";
        if (lower.contains("payable")) return "Liability";
        if (lower.contains("capital") || lower.contains("equity")) return "Equity";
        if (lower.contains("revenue") || lower.contains("sales")) return "Revenue";
        return "Expense";
    }

    public List<ChartOfAccount> getAllAccounts() {
        return accountRepo.findAllByOrderByIdAsc();
    }

    public List<JournalEntry> getAllJournalEntries() {
        return journalRepo.findAllByOrderByEntryDateDescIdDesc();
    }
}

package com.metro.kinetic.model;

import jakarta.persistence.*;

@Entity
@Table(name = "chart_of_accounts")
public class ChartOfAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_name", nullable = false, unique = true)
    private String accountName;

    @Column(name = "account_type", nullable = false)
    private String accountType; // Asset, Liability, Equity, Revenue, Expense

    @Column(name = "balance_inr", nullable = false)
    private Double balanceInr;

    public ChartOfAccount() {
        this.balanceInr = 0.0;
    }

    public ChartOfAccount(String accountName, String accountType, Double balanceInr) {
        this.accountName = accountName;
        this.accountType = accountType;
        this.balanceInr = balanceInr != null ? balanceInr : 0.0;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public Double getBalanceInr() { return balanceInr; }
    public void setBalanceInr(Double balanceInr) { this.balanceInr = balanceInr; }
}

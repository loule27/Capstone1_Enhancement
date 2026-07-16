package com.example.FIle.Manager.DTO;

import java.math.BigDecimal;

public class SummaryDTO {

    private BigDecimal totalDeposits;
    private BigDecimal totalPayments;
    private BigDecimal balance;
    private int transactionCount;

    public SummaryDTO() {
    }
    public SummaryDTO(BigDecimal totalDeposits, BigDecimal totalPayments,
                      BigDecimal balance, int transactionCount) {
        this.totalDeposits = totalDeposits;
        this.totalPayments = totalPayments;
        this.balance = balance;
        this.transactionCount = transactionCount;
    }

    public BigDecimal getTotalDeposits() { return totalDeposits; }
    public void setTotalDeposits(BigDecimal totalDeposits) { this.totalDeposits = totalDeposits; }

    public BigDecimal getTotalPayments() { return totalPayments; }
    public void setTotalPayments(BigDecimal totalPayments) { this.totalPayments = totalPayments; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public int getTransactionCount() { return transactionCount; }
    public void setTransactionCount(int transactionCount) { this.transactionCount = transactionCount; }
}
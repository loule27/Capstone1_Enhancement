package com.example.FIle.Manager.DTO;

import com.example.FIle.Manager.Enums.TransactionType;
import java.math.BigDecimal;

public class TransactionDTO {

    private TransactionType type;
    private String description;
    private String vendor;
    private BigDecimal amount;

    public TransactionDTO() {
    }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getVendor() { return vendor; }
    public void setVendor(String vendor) { this.vendor = vendor; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
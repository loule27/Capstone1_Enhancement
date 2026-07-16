package com.example.FIle.Manager.Models;

import com.example.FIle.Manager.Enums.TransactionType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private String description;

    private String vendor;

    private BigDecimal amount;

    private LocalDateTime timestamp;

    public Transaction() {
    }

    public Transaction(TransactionType type, String description, String vendor,
                       BigDecimal amount, LocalDateTime timestamp) {
        this.type = type;
        this.description = description;
        this.vendor = vendor;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public Long getId() { return id; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getVendor() { return vendor; }
    public void setVendor(String vendor) { this.vendor = vendor; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
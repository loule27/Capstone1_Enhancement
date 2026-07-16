package com.example.FIle.Manager.Repository;

import com.example.FIle.Manager.Enums.TransactionType;
import com.example.FIle.Manager.Models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByType(TransactionType type);

    List<Transaction> findByVendorContainingIgnoreCase(String vendor);

    List<Transaction> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    List<Transaction> findAllByOrderByTimestampDesc();
}
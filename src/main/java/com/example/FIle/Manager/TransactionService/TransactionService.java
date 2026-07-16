package com.example.FIle.Manager.TransactionService;

import com.example.FIle.Manager.DTO.SummaryDTO;
import com.example.FIle.Manager.DTO.TransactionDTO;
import com.example.FIle.Manager.Enums.TransactionType;
import com.example.FIle.Manager.Models.Transaction;
import com.example.FIle.Manager.Repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // CRUD
    public Transaction createTransaction(TransactionDTO dto) {
        if (dto.getType() == null) {
            throw new IllegalArgumentException("Transaction type is required");
        }
        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            throw new IllegalArgumentException("Description is required");
        }
        if (dto.getVendor() == null || dto.getVendor().isBlank()) {
            throw new IllegalArgumentException("Vendor is required");
        }
        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Transaction transaction = new Transaction();
        transaction.setType(dto.getType());
        transaction.setDescription(dto.getDescription());
        transaction.setVendor(dto.getVendor());
        transaction.setAmount(dto.getAmount());
        transaction.setTimestamp(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAllByOrderByTimestampDesc();
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Transaction not found with id: " + id));
    }

    public void deleteTransaction(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "Transaction not found with id: " + id);
        }
        transactionRepository.deleteById(id);
    }

    // Filters

    public List<Transaction> getDeposits() {
        return transactionRepository.findByType(TransactionType.DEPOSIT);
    }

    public List<Transaction> getPayments() {
        return transactionRepository.findByType(TransactionType.DEBIT);
    }

    public List<Transaction> searchByVendor(String vendor) {
        return transactionRepository.findByVendorContainingIgnoreCase(vendor);
    }

    // Reports

    public List<Transaction> getMonthToDate() {
        YearMonth current = YearMonth.now();
        LocalDateTime start = current.atDay(1).atStartOfDay();
        LocalDateTime end = LocalDateTime.now();
        return transactionRepository.findByTimestampBetween(start, end);
    }

    public List<Transaction> getPreviousMonth() {
        YearMonth previous = YearMonth.now().minusMonths(1);
        LocalDateTime start = previous.atDay(1).atStartOfDay();
        LocalDateTime end = previous.atEndOfMonth().atTime(23, 59, 59);
        return transactionRepository.findByTimestampBetween(start, end);
    }

    public List<Transaction> getYearToDate() {
        int currentYear = LocalDate.now().getYear();
        LocalDateTime start = LocalDate.of(currentYear, 1, 1).atStartOfDay();
        LocalDateTime end = LocalDateTime.now();
        return transactionRepository.findByTimestampBetween(start, end);
    }

    public List<Transaction> getPreviousYear() {
        int previousYear = LocalDate.now().getYear() - 1;
        LocalDateTime start = LocalDate.of(previousYear, 1, 1).atStartOfDay();
        LocalDateTime end = LocalDate.of(previousYear, 12, 31).atTime(23, 59, 59);
        return transactionRepository.findByTimestampBetween(start, end);
    }

    // Custom Search

    public List<Transaction> customSearch(String startDate, String endDate,
                                          String description, String vendor,
                                          String type) {
        List<Transaction> all = transactionRepository.findAllByOrderByTimestampDesc();
        List<Transaction> results = new ArrayList<>();

        for (Transaction t : all) {
            boolean passes = true;

            if (startDate != null && !startDate.isBlank()) {
                LocalDate start = LocalDate.parse(startDate);
                if (t.getTimestamp().toLocalDate().isBefore(start)) {
                    passes = false;
                }
            }

            if (endDate != null && !endDate.isBlank()) {
                LocalDate end = LocalDate.parse(endDate);
                if (t.getTimestamp().toLocalDate().isAfter(end)) {
                    passes = false;
                }
            }

            if (description != null && !description.isBlank()) {
                if (!t.getDescription().toLowerCase()
                        .contains(description.toLowerCase())) {
                    passes = false;
                }
            }

            if (vendor != null && !vendor.isBlank()) {
                if (!t.getVendor().toLowerCase()
                        .contains(vendor.toLowerCase())) {
                    passes = false;
                }
            }

            if (type != null && !type.isBlank()) {
                try {
                    TransactionType searchType = TransactionType.valueOf(
                            type.toUpperCase());
                    if (t.getType() != searchType) {
                        passes = false;
                    }
                } catch (IllegalArgumentException e) {
                    // invalid type string — ignore this filter
                }
            }

            if (passes) {
                results.add(t);
            }
        }

        return results;
    }

    // Summary

    public SummaryDTO getSummary() {
        List<Transaction> all = transactionRepository.findAll();

        BigDecimal totalDeposits = BigDecimal.ZERO;
        BigDecimal totalPayments = BigDecimal.ZERO;

        for (Transaction t : all) {
            if (t.getType() == TransactionType.DEPOSIT) {
                totalDeposits = totalDeposits.add(t.getAmount());
            } else {
                totalPayments = totalPayments.add(t.getAmount());
            }
        }

        BigDecimal balance = totalDeposits.subtract(totalPayments);

        return new SummaryDTO(totalDeposits, totalPayments, balance, all.size());
    }

    public SummaryDTO getSummaryForList(List<Transaction> transactions) {
        BigDecimal totalDeposits = BigDecimal.ZERO;
        BigDecimal totalPayments = BigDecimal.ZERO;

        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.DEPOSIT) {
                totalDeposits = totalDeposits.add(t.getAmount());
            } else {
                totalPayments = totalPayments.add(t.getAmount());
            }
        }

        BigDecimal balance = totalDeposits.subtract(totalPayments);

        return new SummaryDTO(totalDeposits, totalPayments,
                balance, transactions.size());
    }
}
package com.example.FIle.Manager.Controller;

import com.example.FIle.Manager.DTO.SummaryDTO;
import com.example.FIle.Manager.DTO.TransactionDTO;
import com.example.FIle.Manager.Models.Transaction;
import com.example.FIle.Manager.TransactionService.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // CRUD

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(
            @RequestBody TransactionDTO dto) {
        Transaction created = transactionService.createTransaction(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    // Filters

    @GetMapping("/deposits")
    public ResponseEntity<List<Transaction>> getDeposits() {
        return ResponseEntity.ok(transactionService.getDeposits());
    }

    @GetMapping("/payments")
    public ResponseEntity<List<Transaction>> getPayments() {
        return ResponseEntity.ok(transactionService.getPayments());
    }

    @GetMapping("/vendor")
    public ResponseEntity<List<Transaction>> searchByVendor(
            @RequestParam String name) {
        return ResponseEntity.ok(transactionService.searchByVendor(name));
    }

    // Reports

    @GetMapping("/reports/month-to-date")
    public ResponseEntity<List<Transaction>> getMonthToDate() {
        return ResponseEntity.ok(transactionService.getMonthToDate());
    }

    @GetMapping("/reports/previous-month")
    public ResponseEntity<List<Transaction>> getPreviousMonth() {
        return ResponseEntity.ok(transactionService.getPreviousMonth());
    }

    @GetMapping("/reports/year-to-date")
    public ResponseEntity<List<Transaction>> getYearToDate() {
        return ResponseEntity.ok(transactionService.getYearToDate());
    }

    @GetMapping("/reports/previous-year")
    public ResponseEntity<List<Transaction>> getPreviousYear() {
        return ResponseEntity.ok(transactionService.getPreviousYear());
    }

    // Custom Search

    @GetMapping("/search")
    public ResponseEntity<List<Transaction>> customSearch(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String vendor,
            @RequestParam(required = false) String type) {
        return ResponseEntity.ok(transactionService.customSearch(
                startDate, endDate, description, vendor, type));
    }

    //Summary
    @GetMapping("/summary")
    public ResponseEntity<SummaryDTO> getSummary() {
        return ResponseEntity.ok(transactionService.getSummary());
    }
}
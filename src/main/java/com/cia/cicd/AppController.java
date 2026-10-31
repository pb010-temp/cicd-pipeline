
package com.cia.cicd;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class AppController {

    private final List<Expense> expenses = new CopyOnWriteArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(10);

    public AppController() {
        seedSampleData();
    }

    public void resetData() {
        expenses.clear();
        idCounter.set(10);
        seedSampleData();
    }

    private void seedSampleData() {
        expenses.add(new Expense(1L, "2026-10-01", "Monthly Salary", "Salary", "Income", 35000.0));
        expenses.add(new Expense(2L, "2026-10-01", "House Rent", "Rent", "Expense", 8000.0));
        expenses.add(new Expense(3L, "2026-10-02", "Groceries", "Food", "Expense", 1200.0));
        expenses.add(new Expense(4L, "2026-10-03", "Bus Ticket", "Transport", "Expense", 50.0));
        expenses.add(new Expense(5L, "2026-10-04", "Netflix Subscription", "Entertainment", "Expense", 199.0));
        expenses.add(new Expense(6L, "2026-10-05", "Electricity Bill", "Bills", "Expense", 900.0));
        expenses.add(new Expense(7L, "2026-10-09", "Freelance Payment", "Other Income", "Income", 5000.0));
        expenses.add(new Expense(8L, "2026-10-11", "Mobile Recharge", "Bills", "Expense", 299.0));
        expenses.add(new Expense(9L, "2026-10-13", "Shopping", "Shopping", "Expense", 1500.0));
        expenses.add(new Expense(10L, "2026-10-15", "Lunch", "Food", "Expense", 180.0));
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }

    @GetMapping("/api/info")
    public Map<String, String> info() {
        return Map.of(
            "application", "Expense Tracker Web App",
            "version", "1.0.0",
            "status", "UP"
        );
    }

    @GetMapping("/api/expenses")
    public List<Expense> getExpenses() {
        return expenses;
    }

    @PostMapping("/api/expenses")
    public ResponseEntity<Expense> addExpense(@RequestBody Expense expense) {
        String desc = expense.getDescription() != null ? expense.getDescription().trim() : "";
        if (desc.isEmpty() && expense.getTitle() != null) {
            desc = expense.getTitle().trim();
        }

        if (desc.isEmpty() || expense.getAmount() <= 0) {
            return ResponseEntity.badRequest().build();
        }

        long id = idCounter.incrementAndGet();
        expense.setId(id);
        expense.setDescription(desc);

        if (expense.getCategory() == null || expense.getCategory().trim().isEmpty()) {
            expense.setCategory("Other");
        } else {
            expense.setCategory(expense.getCategory().trim());
        }

        if (expense.getType() == null || (!expense.getType().equalsIgnoreCase("Income") && !expense.getType().equalsIgnoreCase("Expense"))) {
            expense.setType("Expense");
        } else {
            expense.setType(expense.getType().substring(0, 1).toUpperCase() + expense.getType().substring(1).toLowerCase());
        }

        if (expense.getDate() == null || expense.getDate().trim().isEmpty()) {
            expense.setDate(LocalDate.now().toString());
        }

        expenses.add(expense);
        return ResponseEntity.status(HttpStatus.CREATED).body(expense);
    }

    @DeleteMapping("/api/expenses/{id}")
    public ResponseEntity<Map<String, Object>> deleteExpense(@PathVariable Long id) {
        boolean removed = expenses.removeIf(e -> e.getId().equals(id));
        if (removed) {
            return ResponseEntity.ok(Map.of("success", true, "deletedId", id));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Transaction not found"));
        }
    }

    @GetMapping("/api/summary")
    public Map<String, Object> getSummary() {
        double totalIncome = expenses.stream()
                .filter(e -> "Income".equalsIgnoreCase(e.getType()))
                .mapToDouble(Expense::getAmount)
                .sum();

        double totalExpenses = expenses.stream()
                .filter(e -> "Expense".equalsIgnoreCase(e.getType()))
                .mapToDouble(Expense::getAmount)
                .sum();

        double remainingBalance = totalIncome - totalExpenses;
        long count = expenses.size();

        return Map.of(
            "totalIncome", Math.round(totalIncome * 100.0) / 100.0,
            "totalExpenses", Math.round(totalExpenses * 100.0) / 100.0,
            "remainingBalance", Math.round(remainingBalance * 100.0) / 100.0,
            "count", count
        );
    }
}

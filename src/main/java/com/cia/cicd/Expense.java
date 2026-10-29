package com.cia.cicd;

public class Expense {

    private Long id;
    private String date;
    private String description;
    private String category;
    private String type; // "Income" or "Expense"
    private double amount;

    public Expense() {
    }

    public Expense(Long id, String date, String description, String category, String type, double amount) {
        this.id = id;
        this.date = date;
        this.description = description;
        this.category = category;
        this.type = type;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Alias for compatibility if title is used
    public String getTitle() {
        return description;
    }

    public void setTitle(String title) {
        if (this.description == null || this.description.isEmpty()) {
            this.description = title;
        }
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type != null ? type : "Expense";
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}

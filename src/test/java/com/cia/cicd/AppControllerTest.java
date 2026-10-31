
package com.cia.cicd;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppController.class)
class AppControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AppController appController;

    @BeforeEach
    void setUp() {
        appController.resetData();
    }

    @Test
    void healthEndpointReturnsUpStatus() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void infoEndpointReturnsApplicationMetadata() throws Exception {
        mockMvc.perform(get("/api/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.application").value("Expense Tracker Web App"))
                .andExpect(jsonPath("$.version").value("1.0.0"))
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void getExpensesReturnsSeedTransactions() throws Exception {
        mockMvc.perform(get("/api/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)))
                .andExpect(jsonPath("$[0].description").value("Monthly Salary"))
                .andExpect(jsonPath("$[0].type").value("Income"))
                .andExpect(jsonPath("$[0].amount").value(35000.0));
    }

    @Test
    void summaryEndpointReturnsInitialCalculations() throws Exception {
        mockMvc.perform(get("/api/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIncome").value(40000.0))
                .andExpect(jsonPath("$.totalExpenses").value(12328.0))
                .andExpect(jsonPath("$.remainingBalance").value(27672.0))
                .andExpect(jsonPath("$.count").value(10));
    }

    @Test
    void addExpenseAddsAndReturnsCreatedExpense() throws Exception {
        Expense newExpense = new Expense(null, "2026-10-29", "Coffee", "Food", "Expense", 80.0);

        mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newExpense)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(11))
                .andExpect(jsonPath("$.description").value("Coffee"))
                .andExpect(jsonPath("$.type").value("Expense"))
                .andExpect(jsonPath("$.amount").value(80.0));
    }

    @Test
    void addExpenseWithInvalidDataReturnsBadRequest() throws Exception {
        Expense invalidExpense = new Expense(null, "2026-10-29", "", "Food", "Expense", -10.0);

        mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidExpense)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteExpenseRemovesItem() throws Exception {
        mockMvc.perform(delete("/api/expenses/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(get("/api/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(9))
                .andExpect(jsonPath("$.totalExpenses").value(12278.0));
    }
}

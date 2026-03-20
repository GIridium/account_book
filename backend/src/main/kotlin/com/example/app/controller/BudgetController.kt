package com.example.app.controller

import com.example.app.dto.SetBudgetRequest
import com.example.app.model.Budget
import com.example.app.service.BudgetService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/budgets")
class BudgetController(
    private val service: BudgetService
) {
    @GetMapping
    fun list(): List<Budget> = service.getAllBudgets()

    @PostMapping
    fun setBudget(@Valid @RequestBody req: SetBudgetRequest): Budget {
        return service.setBudget(req)
    }

    @DeleteMapping("/{category}")
    fun delete(@PathVariable category: String): ResponseEntity<Map<String, String>> {
        service.deleteBudget(category)
        return ResponseEntity.ok(mapOf("message" to "Budget deleted"))
    }
}


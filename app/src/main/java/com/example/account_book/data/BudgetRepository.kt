package com.example.account_book.data

import com.example.account_book.model.Budget
import kotlin.collections.plus

object BudgetRepository {
    private var budgets: List<Budget> = emptyList()

    fun getBudgets(): List<Budget> = budgets

    fun setBudgets(newBudgets: List<Budget>) { budgets = newBudgets }

    fun addBudget(budget: Budget) { budgets = budgets + budget }

    fun clearBudgets() { budgets = emptyList()
    }

}
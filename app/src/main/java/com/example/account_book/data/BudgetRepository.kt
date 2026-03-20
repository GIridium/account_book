package com.example.account_book.data

import com.example.account_book.model.Budget
import com.example.account_book.network.RetrofitClient // Added import

object BudgetRepository {

    suspend fun getBudgets(): List<Budget> {
        return try {
            RetrofitClient.apiService.getBudgets()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun saveBudget(budget: Budget) {
         try {
            RetrofitClient.apiService.setBudget(budget)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

   suspend fun deleteBudget(category: String) {
        try {
            RetrofitClient.apiService.deleteBudget(category)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun syncBudgets(newBudgets: List<Budget>) {
        val currentBudgets = getBudgets()
        val currentCategories = currentBudgets.map { it.category }.toSet()
        val newCategories = newBudgets.map { it.category }.toSet()

        // Delete removed
        currentCategories.minus(newCategories).forEach { category ->
            deleteBudget(category)
        }

        // Save/Update all in the new list
        newBudgets.forEach { budget ->
            saveBudget(budget)
        }
    }

}
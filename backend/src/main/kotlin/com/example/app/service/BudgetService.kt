package com.example.app.service

import com.example.app.dto.SetBudgetRequest
import com.example.app.model.Budget
import com.example.app.model.BudgetEntity
import com.example.app.model.UserEntity
import com.example.app.repository.BudgetRepository
import com.example.app.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.Instant

@Service
class BudgetService(
    private val budgetRepository: BudgetRepository,
    private val userRepository: UserRepository
) {

    // Ideally this should be retrieved from SecurityContext
    // Using simple approach as requested to follow existing pattern from TransactionService
    private val defaultUsername = "default_user"

    private fun getDefaultUser(): UserEntity {
        return userRepository.findByUsername(defaultUsername)
            ?: throw IllegalStateException("Default user not found. Please ensure application is initialized.")
    }

    @Transactional(readOnly = true)
    fun getAllBudgets(): List<Budget> {
        val user = getDefaultUser()
        return budgetRepository.findByUser(user).map {
            Budget(
                category = it.categoryName,
                limit = it.limitAmount,
                spent = BigDecimal.ZERO // Frontend calculates spent
            )
        }
    }

    @Transactional
    fun setBudget(req: SetBudgetRequest): Budget {
        val user = getDefaultUser()
        val existing = budgetRepository.findByUserAndCategoryName(user, req.category)

        val entity = if (existing.isPresent) {
            val budget = existing.get()
            budget.limitAmount = req.limit
            budget.updatedAt = Instant.now()
            budget
        } else {
            BudgetEntity(
                categoryName = req.category,
                limitAmount = req.limit,
                user = user
            )
        }

        val saved = budgetRepository.save(entity)
        return Budget(saved.categoryName, saved.limitAmount)
    }

    @Transactional
    fun deleteBudget(category: String) {
        val user = getDefaultUser()
        val existing = budgetRepository.findByUserAndCategoryName(user, category)
        if (existing.isPresent) {
            budgetRepository.delete(existing.get())
        }
    }
}


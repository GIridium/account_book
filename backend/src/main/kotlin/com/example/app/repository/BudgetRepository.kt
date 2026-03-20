package com.example.app.repository

import com.example.app.model.BudgetEntity
import com.example.app.model.UserEntity
import java.util.Optional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BudgetRepository : JpaRepository<BudgetEntity, Long> {
    fun findByUser(user: UserEntity): List<BudgetEntity>
    fun findByUserAndCategoryName(user: UserEntity, categoryName: String): Optional<BudgetEntity>
    fun deleteByUserAndCategoryName(user: UserEntity, categoryName: String)
}


package com.example.app.service

import com.example.app.dto.CreateTransactionRequest
import com.example.app.dto.UpdateTransactionRequest
import com.example.app.model.BillEntity
import com.example.app.model.CategoryEntity
import com.example.app.model.Transaction
import com.example.app.model.UserEntity
import com.example.app.repository.BillRepository
import com.example.app.repository.CategoryRepository
import com.example.app.repository.UserRepository
import jakarta.annotation.PostConstruct
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@Service
class TransactionService(
    private val billRepository: BillRepository,
    private val categoryRepository: CategoryRepository,
    private val userRepository: UserRepository
) {
    // Default user for prototyping - replace with SecurityContext later
    private val defaultUsername = "default_user"

    @PostConstruct
    fun init() {
        if (userRepository.findByUsername(defaultUsername) == null) {
            userRepository.save(
                UserEntity(
                    username = defaultUsername,
                    passwordHash = "demo_hash",
                    nickname = "Demo User"
                )
            )
        }
    }

    private fun getDefaultUser(): UserEntity {
        return userRepository.findByUsername(defaultUsername)
            ?: throw IllegalStateException("Default user not found")
    }

    @Transactional(readOnly = true)
    fun findAll(): List<Transaction> = billRepository.findAllWithDetails().map { it.toTransaction() }

    @Transactional(readOnly = true)
    fun findById(id: Long): Transaction {
        return billRepository.findById(id)
            .map { it.toTransaction() }
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found") }
    }

    @Transactional
    fun create(req: CreateTransactionRequest): Transaction {
        val user = getDefaultUser()
        val category = categoryRepository.findById(req.categoryId!!)
            .orElseThrow { ResponseStatusException(HttpStatus.BAD_REQUEST, "Category not found") }

        val entity = BillEntity(
            amount = req.amount!!,
            category = category,
            billDate = req.date!!,
            remark = req.remark?.trim(),
            merchant = req.merchant?.trim(),
            user = user
        )
        return billRepository.save(entity).toTransaction()
    }

    @Transactional
    fun update(id: Long, req: UpdateTransactionRequest): Transaction {
        val entity = billRepository.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found") }

        val category = categoryRepository.findById(req.categoryId!!)
            .orElseThrow { ResponseStatusException(HttpStatus.BAD_REQUEST, "Category not found") }

        entity.amount = req.amount!!
        entity.category = category
        entity.billDate = req.date!!
        entity.remark = req.remark?.trim()
        entity.merchant = req.merchant?.trim()
        // billRepository.save(entity) is not strictly needed in @Transactional, but good for explicit return
        return billRepository.save(entity).toTransaction()
    }

    @Transactional
    fun delete(id: Long) {
        if (!billRepository.existsById(id)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found")
        }
        billRepository.deleteById(id)
    }
}


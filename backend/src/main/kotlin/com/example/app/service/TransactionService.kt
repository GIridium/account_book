package com.example.app.service

import com.example.app.dto.CreateTransactionRequest
import com.example.app.dto.UpdateTransactionRequest
import com.example.app.model.Transaction
import com.example.app.repository.InMemoryTransactionRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@Service
class TransactionService(
    private val repository: InMemoryTransactionRepository
) {
    fun findAll(): List<Transaction> = repository.findAll()

    fun findById(id: Long): Transaction =
        repository.findById(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "transaction not found")

    fun create(req: CreateTransactionRequest): Transaction {
        val template = Transaction(
            id = 0L,
            amount = req.amount!!,
            category = req.category!!.trim(),
            note = req.note?.trim(),
            date = req.date!!,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        return repository.create(template)
    }

    fun update(id: Long, req: UpdateTransactionRequest): Transaction {
        val updated = Transaction(
            id = id,
            amount = req.amount!!,
            category = req.category!!.trim(),
            note = req.note?.trim(),
            date = req.date!!,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        return repository.update(id, updated)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "transaction not found")
    }

    fun delete(id: Long) {
        val deleted = repository.delete(id)
        if (!deleted) throw ResponseStatusException(HttpStatus.NOT_FOUND, "transaction not found")
    }
}
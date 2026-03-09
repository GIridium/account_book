package com.example.app.repository

import com.example.app.model.Transaction
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@Repository
class InMemoryTransactionRepository {
    private val data = ConcurrentHashMap<Long, Transaction>()
    private val idGen = AtomicLong(0)

    fun findAll(): List<Transaction> =
        data.values.sortedByDescending { it.date }

    fun findById(id: Long): Transaction? = data[id]

    fun save(newTx: Transaction): Transaction {
        data[newTx.id] = newTx
        return newTx
    }

    fun create(template: Transaction): Transaction {
        val id = idGen.incrementAndGet()
        val now = Instant.now()
        val tx = template.copy(id = id, createdAt = now, updatedAt = now)
        data[id] = tx
        return tx
    }

    fun update(id: Long, updated: Transaction): Transaction? {
        val old = data[id] ?: return null
        val merged = updated.copy(id = id, createdAt = old.createdAt, updatedAt = Instant.now())
        data[id] = merged
        return merged
    }

    fun delete(id: Long): Boolean = data.remove(id) != null
}
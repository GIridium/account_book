// app/src/main/java/com/example/account_book/data/TransactionRepository.kt
package com.example.account_book.data

import com.example.account_book.model.Transaction
import com.example.account_book.model.TransactionType
import java.util.*

object TransactionRepository {
    // Removed in-memory list and nextId counter

    suspend fun addTransaction(transaction: Transaction) {
        try {
            val networkTransaction = com.example.account_book.utils.Mapper.toNetworkTransaction(transaction)
            com.example.account_book.network.RetrofitClient.apiService.createTransaction(networkTransaction)
        } catch (e: Exception) {
            e.printStackTrace() // Log error
        }
    }

    suspend fun updateTransaction(transaction: Transaction) {
        try {
            val networkTransaction = com.example.account_book.utils.Mapper.toNetworkTransaction(transaction)
            com.example.account_book.network.RetrofitClient.apiService.updateTransaction(transaction.id, networkTransaction)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteTransaction(id: Long) {
        try {
            com.example.account_book.network.RetrofitClient.apiService.deleteTransaction(id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getAllTransactions(): List<Transaction> {
        return try {
            val networkTransactions = com.example.account_book.network.RetrofitClient.apiService.getTransactions()
            networkTransactions.map { com.example.account_book.utils.Mapper.toTransaction(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getTransaction(id: Long): Transaction? {
        return try {
            val networkTransaction = com.example.account_book.network.RetrofitClient.apiService.getTransaction(id)
            com.example.account_book.utils.Mapper.toTransaction(networkTransaction)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getTransactionsByDateRange(start: Date, end: Date): List<Transaction> {
        val allTransactions = getAllTransactions()
        return allTransactions.filter { transaction ->
            transaction.date.time in start.time..end.time
        }
    }

    suspend fun getTodayTransactions(): List<Transaction> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.time

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfDay = calendar.time

        return getTransactionsByDateRange(startOfDay, endOfDay)
    }

    suspend fun getThisWeekTransactions(): List<Transaction> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfWeek = calendar.time

        calendar.add(Calendar.WEEK_OF_YEAR, 1)
        calendar.add(Calendar.MILLISECOND, -1)
        val endOfWeek = calendar.time

        return getTransactionsByDateRange(startOfWeek, endOfWeek)
    }

    suspend fun getThisMonthTransactions(): List<Transaction> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfMonth = calendar.time

        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.MILLISECOND, -1)
        val endOfMonth = calendar.time

        return getTransactionsByDateRange(startOfMonth, endOfMonth)
    }

    suspend fun getCustomRangeTransactions(start: Date, end: Date): List<Transaction> {
        return getTransactionsByDateRange(start, end)
    }

    // 修复：添加无参数重载版本
    suspend fun getSummary(): Summary {
        return getSummary(getAllTransactions())
    }

    fun getSummary(transactions: List<Transaction>): Summary {
        var totalIncome = 0.0
        var totalExpense = 0.0

        transactions.forEach { transaction ->
            when (transaction.type) {
                TransactionType.INCOME -> totalIncome += transaction.amount
                TransactionType.EXPENSE -> totalExpense += transaction.amount
            }
        }

        return Summary(totalIncome, totalExpense)
    }

    data class Summary(
        val totalIncome: Double,
        val totalExpense: Double
    )
}
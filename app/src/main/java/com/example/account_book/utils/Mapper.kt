package com.example.account_book.utils

import com.example.account_book.model.Category
import com.example.account_book.model.Transaction
import com.example.account_book.model.TransactionType
import com.example.account_book.network.model.NetworkTransaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Mapper {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun toTransaction(networkTransaction: NetworkTransaction): Transaction {
        return Transaction(
            id = networkTransaction.id ?: 0,
            amount = networkTransaction.amount,
            category = mapIdToCategory(networkTransaction.categoryId),
            note = networkTransaction.remark ?: "",
            merchant = networkTransaction.merchant ?: "",
            type = TransactionType.EXPENSE, // Defaulting to Expense as API doesn't specify
            date = try {
                dateFormat.parse(networkTransaction.date) ?: Date()
            } catch (e: Exception) {
                Date()
            }
        )
    }

    fun toNetworkTransaction(transaction: Transaction): NetworkTransaction {
        return NetworkTransaction(
            // id is likely ignored by backend on create
            amount = transaction.amount,
            categoryId = mapCategoryToId(transaction.category),
            remark = transaction.note,
            merchant = transaction.merchant,
            date = dateFormat.format(transaction.date)
        )
    }

    private fun mapIdToCategory(id: Long): Category {
        return when (id) {
            1L -> Category.FOOD
            2L -> Category.TRANSPORT
            3L -> Category.SHOPPING
            4L -> Category.ENTERTAINMENT
            5L -> Category.STUDY
            6L -> Category.HOUSING
            7L -> Category.MEDICAL
            8L -> Category.OTHER
            else -> Category.OTHER
        }
    }

    private fun mapCategoryToId(category: Category): Long {
        return when (category) {
            Category.FOOD -> 1L
            Category.TRANSPORT -> 2L
            Category.SHOPPING -> 3L
            Category.ENTERTAINMENT -> 4L
            Category.STUDY -> 5L
            Category.HOUSING -> 6L
            Category.MEDICAL -> 7L
            Category.OTHER -> 8L
        }
    }
}


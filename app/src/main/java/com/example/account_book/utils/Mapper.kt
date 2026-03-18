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
        val category = mapIdToCategory(networkTransaction.categoryId)
        val transactionType = try {
            if (networkTransaction.type != null) {
                TransactionType.valueOf(networkTransaction.type)
            } else {
                category.type
            }
        } catch (e: IllegalArgumentException) {
            category.type
        }

        return Transaction(
            id = networkTransaction.id ?: 0L,
            amount = networkTransaction.amount,
            category = category,
            note = networkTransaction.remark ?: "",
            merchant = networkTransaction.merchant ?: "",
            type = transactionType,
            date = try {
                dateFormat.parse(networkTransaction.date) ?: Date()
            } catch (e: Exception) {
                Date()
            }
        )
    }

    fun toNetworkTransaction(transaction: Transaction): NetworkTransaction {
        return NetworkTransaction(
            id = if (transaction.id == 0L) null else transaction.id,
            amount = transaction.amount,
            categoryId = mapCategoryToId(transaction.category),
            remark = transaction.note,
            merchant = transaction.merchant,
            type = transaction.type.name,
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
            9L -> Category.SALARY_INCOME
            10L -> Category.OVERTIME_INCOME
            11L -> Category.BONUS_INCOME
            12L -> Category.PART_TIME_INCOME
            13L -> Category.BUSINESS_INCOME
            14L -> Category.INVESTMENT_INCOME
            15L -> Category.GIFT_INCOME
            16L -> Category.OTHER_INCOME
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
            Category.SALARY_INCOME -> 9L
            Category.OVERTIME_INCOME -> 10L
            Category.BONUS_INCOME -> 11L
            Category.PART_TIME_INCOME -> 12L
            Category.BUSINESS_INCOME -> 13L
            Category.INVESTMENT_INCOME -> 14L
            Category.GIFT_INCOME -> 15L
            Category.OTHER_INCOME -> 16L
        }
    }
}
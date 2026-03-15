// app/src/main/java/com/example/account_book/model/Transaction.kt
package com.example.account_book.model

import android.os.Parcelable
import java.util.Date
import kotlinx.parcelize.Parcelize

@Parcelize
enum class TransactionType : Parcelable {
    INCOME, EXPENSE
}

@Parcelize
data class Transaction(
    val id: Int = 0,
    val amount: Double = 0.0,
    val category: Category = Category.OTHER,
    val note: String = "",
    val merchant: String = "", // Added merchant field
    val type: TransactionType = TransactionType.EXPENSE,
    val date: Date = Date()
) : Parcelable
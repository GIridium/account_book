// app/src/main/java/com/example/account_book/model/Category.kt
package com.example.account_book.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Category(val displayName: String, val type: TransactionType) : Parcelable {
    // 支出 (Expenses)
    FOOD("餐饮", TransactionType.EXPENSE),
    TRANSPORT("交通", TransactionType.EXPENSE),
    SHOPPING("购物", TransactionType.EXPENSE),
    ENTERTAINMENT("娱乐", TransactionType.EXPENSE),
    STUDY("学习", TransactionType.EXPENSE),
    HOUSING("住房", TransactionType.EXPENSE),
    MEDICAL("医疗", TransactionType.EXPENSE),
    OTHER("其他", TransactionType.EXPENSE),

    // 收入 (Incomes)
    SALARY("工资收入", TransactionType.INCOME),
    OVERTIME("加班收入", TransactionType.INCOME),
    BONUS("奖金收入", TransactionType.INCOME),
    PART_TIME("兼职收入", TransactionType.INCOME),
    BUSINESS("经营所得", TransactionType.INCOME),
    INVESTMENT("投资收入", TransactionType.INCOME),
    GIFT("礼金收入", TransactionType.INCOME),
    OTHER_INCOME("其他", TransactionType.INCOME);

    companion object {
        fun fromDisplayName(name: String): Category {
            return entries.find { it.displayName == name } ?: OTHER
        }

        fun fromDisplayName(name: String, type: TransactionType): Category {
            return entries.find { it.displayName == name && it.type == type }
                ?: if (type == TransactionType.EXPENSE) OTHER else OTHER_INCOME
        }
    }
}
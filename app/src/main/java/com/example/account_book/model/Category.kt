// app/src/main/java/com/example/account_book/model/Category.kt
package com.example.account_book.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Category(val displayName: String, val type: TransactionType) : Parcelable {
    // 支出分类
    FOOD("餐饮", TransactionType.EXPENSE),
    TRANSPORT("交通", TransactionType.EXPENSE),
    SHOPPING("购物", TransactionType.EXPENSE),
    ENTERTAINMENT("娱乐", TransactionType.EXPENSE),
    STUDY("学习", TransactionType.EXPENSE),
    HOUSING("住房", TransactionType.EXPENSE),
    MEDICAL("医疗", TransactionType.EXPENSE),

    // 收入分类
    SALARY_INCOME("工资收入", TransactionType.INCOME),
    OVERTIME_INCOME("加班收入", TransactionType.INCOME),
    BONUS_INCOME("奖金收入", TransactionType.INCOME),
    PART_TIME_INCOME("兼职收入", TransactionType.INCOME),
    BUSINESS_INCOME("经营所得", TransactionType.INCOME),
    INVESTMENT_INCOME("投资收入", TransactionType.INCOME),
    GIFT_INCOME("礼金收入", TransactionType.INCOME),

    // 通用分类（收入/支出都可用）
    OTHER("其他", TransactionType.EXPENSE),
    OTHER_INCOME("其他", TransactionType.INCOME);

    companion object {
        fun fromDisplayName(name: String, type: TransactionType = TransactionType.EXPENSE): Category {
            return entries.find { it.displayName == name && it.type == type } ?: OTHER
        }
    }
}
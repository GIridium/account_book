// app/src/main/java/com/example/account_book/model/Category.kt
package com.example.account_book.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Category(val displayName: String) : Parcelable {
    // 支出分类
    FOOD("餐饮"),
    TRANSPORT("交通"),
    SHOPPING("购物"),
    ENTERTAINMENT("娱乐"),
    STUDY("学习"),
    HOUSING("住房"),
    MEDICAL("医疗"),

    // 收入分类
    SALARY_INCOME("工资收入"),
    OVERTIME_INCOME("加班收入"),
    BONUS_INCOME("奖金收入"),
    PART_TIME_INCOME("兼职收入"),
    BUSINESS_INCOME("经营所得"),
    INVESTMENT_INCOME("投资收入"),
    GIFT_INCOME("礼金收入"),

    // 通用分类（收入/支出都可用）
    OTHER("其他");

    companion object {
        fun fromDisplayName(name: String): Category {
            return values().find { it.displayName == name } ?: OTHER
        }
    }
}
// app/src/main/java/com/example/account_book/model/Category.kt
package com.example.account_book.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Category(val displayName: String) : Parcelable {  // 添加 : Parcelable
    FOOD("餐饮"),
    TRANSPORT("交通"),
    SHOPPING("购物"),
    ENTERTAINMENT("娱乐"),
    STUDY("学习"),
    HOUSING("住房"),
    MEDICAL("医疗"),
    OTHER("其他");

    companion object {
        fun fromDisplayName(name: String): Category {
            return values().find { it.displayName == name } ?: OTHER
        }
    }
}
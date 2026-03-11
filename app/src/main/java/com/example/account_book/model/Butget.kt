
// app/src/main/java/com/example/account_book/model/Budget.kt

package com.example.account_book.model

data class Budget(
    val category: String, // 类别名，比如"餐饮"
    val limit: Float,     // 预算上限
    val spent: Float      // 已花费金额
)
package com.example.app.model

import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

data class Transaction(
    val id: Long,
    val amount: BigDecimal,
    val categoryId: Long,
    val categoryName: String,
    val type: String,
    val remark: String?,
    val merchant: String?,
    val date: LocalDate,
    val createdAt: Instant
)
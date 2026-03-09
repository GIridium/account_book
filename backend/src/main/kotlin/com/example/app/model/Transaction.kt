package com.example.app.model

import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

data class Transaction(
    val id: Long,
    val amount: BigDecimal,
    val category: String,
    val note: String?,
    val date: LocalDate,
    val createdAt: Instant,
    val updatedAt: Instant
)
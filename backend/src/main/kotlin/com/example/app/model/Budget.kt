package com.example.app.model

import java.math.BigDecimal

data class Budget(
    val category: String, // Budget category name
    val limit: BigDecimal,
    val spent: BigDecimal = BigDecimal.ZERO // Optional: could be 0 as frontend calculates it
)


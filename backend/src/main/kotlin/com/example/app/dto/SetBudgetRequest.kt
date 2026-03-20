package com.example.app.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import java.math.BigDecimal

data class SetBudgetRequest(
    @field:NotBlank
    val category: String,

    @field:DecimalMin("0.0")
    val limit: BigDecimal
)


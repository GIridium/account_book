package com.example.app.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDate

data class UpdateTransactionRequest(
    @field:NotNull
    @field:DecimalMin(value = "0.01", message = "amount must be greater than 0")
    val amount: BigDecimal?,

    @field:NotBlank(message = "category cannot be blank")
    val category: String?,

    val note: String?,

    @field:NotNull
    val date: LocalDate?
)
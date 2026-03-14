package com.example.app.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDate

data class CreateTransactionRequest(
    @field:NotNull
    @field:DecimalMin(value = "0.01", message = "amount must be greater than 0")
    val amount: BigDecimal?,

    @field:NotNull(message = "categoryId cannot be null")
    val categoryId: Long?,

    val remark: String?,

    val merchant: String?,

    @field:NotNull
    val date: LocalDate?
)


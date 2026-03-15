package com.example.account_book.network.model

import com.google.gson.annotations.SerializedName

data class NetworkTransaction(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("amount") val amount: Double,
    @SerializedName("categoryId") val categoryId: Long,
    @SerializedName("remark") val remark: String?,
    @SerializedName("merchant") val merchant: String?,
    @SerializedName("date") val date: String // Format: YYYY-MM-DD
)


package com.example.account_book.network

import com.example.account_book.network.model.NetworkTransaction
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("api/transactions")
    suspend fun getTransactions(): List<NetworkTransaction>

    @POST("api/transactions")
    suspend fun createTransaction(@Body transaction: NetworkTransaction): NetworkTransaction
}


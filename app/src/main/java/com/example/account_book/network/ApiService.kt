package com.example.account_book.network

import com.example.account_book.model.Budget
import com.example.account_book.network.model.NetworkTransaction
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("api/transactions")
    suspend fun getTransactions(): List<NetworkTransaction>

    @POST("api/transactions")
    suspend fun createTransaction(@Body transaction: NetworkTransaction): NetworkTransaction

    @GET("api/transactions/{id}")
    suspend fun getTransaction(@Path("id") id: Long): NetworkTransaction

    @PUT("api/transactions/{id}")
    suspend fun updateTransaction(@Path("id") id: Long, @Body transaction: NetworkTransaction): NetworkTransaction

    @DELETE("api/transactions/{id}")
    suspend fun deleteTransaction(@Path("id") id: Long)

    @GET("api/budgets")
    suspend fun getBudgets(): List<Budget>

    @POST("api/budgets")
    suspend fun setBudget(@Body budget: Budget): Budget

    @DELETE("api/budgets/{category}")
    suspend fun deleteBudget(@Path("category") category: String): retrofit2.Response<Unit>
}

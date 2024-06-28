package com.rikikunproject.duinocoins.network

import com.rikikunproject.duinocoins.model.SuccessResponse
import com.rikikunproject.duinocoins.model.TransactionResponses
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    // Contoh endpoint GET, ganti URL sesuai kebutuhan Anda
    @GET("users/{username}")
    fun getUser(@Path("username") username: String): Call<SuccessResponse>

    @GET("v2/users/{username}")
    fun getFullUser(@Path("username") username: String): Call<SuccessResponse>

    @GET("users/{username}")
    fun getUserWidget(@Path("username") username: String): Call<SuccessResponse>

    @GET("id_transactions/{id}")
    fun getTransaction(@Path("id") id: Int): Call<TransactionResponses>

    // Tambahkan endpoint-endpoint lain sesuai kebutuhan
}
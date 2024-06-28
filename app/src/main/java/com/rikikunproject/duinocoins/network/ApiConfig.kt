package com.rikikunproject.duinocoins.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://server.duinocoin.com/") // Ganti URL base sesuai API Anda
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
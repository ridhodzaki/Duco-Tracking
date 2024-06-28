package com.rikikunproject.duinocoins.utils

import com.rikikunproject.duinocoins.model.SuccessResponse
import com.rikikunproject.duinocoins.network.ApiConfig.apiService

object ApiUtils {
    suspend fun getUserData(username: String): SuccessResponse? {
        try {
            val response = apiService.getUser(username).execute()
            if (response.isSuccessful) {
                return response.body()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}
package com.rikikunproject.duinocoins

import SpinnerLoader
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import com.rikikunproject.duinocoins.components.GlobalDialog.showAlert
import com.rikikunproject.duinocoins.databinding.ActivityMainBinding
import com.rikikunproject.duinocoins.model.SuccessResponse
import com.rikikunproject.duinocoins.network.ApiConfig.apiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(R.layout.activity_main)
        setContentView(binding.root)
        supportActionBar?.hide()

        val sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
        val savedUsername = sharedPreferences.getString("username", null)

        if (savedUsername != null) {
            // Jika ada username, arahkan ke DashboardActivity
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.edtUsername.setOnEditorActionListener { _, actionId, keyEvent ->
            if (actionId == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_DOWN) {
                // Panggil fungsi Anda di sini
                Log.e(TAG, "tombol enter jalan di set editor")
                login()
                return@setOnEditorActionListener true
            }
            false
        }

        binding.edtUsername.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                // Tindakan yang akan dilakukan ketika tombol "Enter" ditekan
                Log.e(TAG, "tombol enter jalan di set key")
                login()
                return@setOnKeyListener true
            }
            false
        }

        binding.btnCheck.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val username = binding.edtUsername.text.toString().trim()

        if (username.isNotEmpty()) {
            SpinnerLoader.show(this)
            Log.e(TAG, username)
            val call = apiService.getUser(username)
            call.enqueue(object : Callback<SuccessResponse> {
                override fun onResponse(call: Call<SuccessResponse>, response: Response<SuccessResponse>) {
                    try {
                        if (response.isSuccessful) {
                            val apiResponse = response.body()
                            Log.e(TAG, response.body().toString())
                            if (apiResponse!!.success) {
                                runOnUiThread {
                                    SpinnerLoader.hide()
                                    val sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
                                    val editor = sharedPreferences.edit()
                                    editor.putString("username", username)
                                    editor.apply()
                                    var intent = Intent(this@MainActivity, DashboardActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            } else {
                                runOnUiThread {
                                    showAlert(this@MainActivity, "Oops..", apiResponse.message)
                                    SpinnerLoader.hide()
                                }
                            }
                            // Proses data sukses
                        }
                    } finally {
                        // Sembunyikan spinner loading setelah panggilan API selesai
//                            SpinnerLoader.hide()
                    }
                }

                override fun onFailure(call: Call<SuccessResponse>, t: Throwable) {
                    // Tangani kesalahan jaringan atau request
                    SpinnerLoader.hide()
                    if (t is IOException) {
                        // Kegagalan jaringan, misalnya tidak ada koneksi internet
                        showAlert(this@MainActivity, "Network Error", "Check your internet connection")
                    } else {
                        // Kegagalan lainnya, termasuk kegagalan request ke server
                        showAlert(this@MainActivity, "Request Error", "Failed to fetch data from server")
                    }
                }
            })
        } else {
            showAlert(this, "Oops...", "Username Tidak Boleh Kosong")
        }
    }
}

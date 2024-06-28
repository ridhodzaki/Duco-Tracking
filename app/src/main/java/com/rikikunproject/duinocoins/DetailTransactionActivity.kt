package com.rikikunproject.duinocoins

import SpinnerLoader
import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.rikikunproject.duinocoins.components.GlobalDialog
import com.rikikunproject.duinocoins.databinding.ActivityDetailTransactionBinding
import com.rikikunproject.duinocoins.model.SuccessResponse
import com.rikikunproject.duinocoins.model.TransactionResponses
import com.rikikunproject.duinocoins.network.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class DetailTransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailTransactionBinding
    private var id = 0
    private lateinit var sharedPreferences: SharedPreferences
    private var savedUsername = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTransactionBinding.inflate(layoutInflater)
//        setContentView(R.layout.activity_detail_transaction)
        setContentView(binding.root)
        supportActionBar?.hide()
        sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
        savedUsername = sharedPreferences.getString("username", null).toString()
        id = intent.getIntExtra("id", 0)

        binding.btnBack.setOnClickListener {
            this.finish()
        }

        binding.btnInfo.setOnClickListener {
            var intent = Intent(this, InfoActivity::class.java)
            startActivity(intent)
        }

        SpinnerLoader.show(this)
        getData()
    }

    private fun getData() {
        val call = id?.let { ApiConfig.apiService.getTransaction(it) }
        call?.enqueue(object : Callback<TransactionResponses> {
            override fun onResponse(call: Call<TransactionResponses>, response: Response<TransactionResponses>) {
                try {
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        Log.e(ContentValues.TAG, response.body().toString())
                        if (apiResponse!!.success) {
                            runOnUiThread {
                                // Code untuk memanggil adapter
                                binding.textMemo.text = "\"${apiResponse.result.memo}\""
                                binding.textDatetime.text = "${apiResponse.result.datetime}"
                                binding.textId.text = "${apiResponse.result.id}"
                                binding.textSender.text = "${apiResponse.result.sender}"
                                binding.textDuco.text = "${apiResponse.result.amount} DUCO"
                                if (apiResponse.result.recipient === savedUsername) {
                                    binding.textStatus.text = "sent successfully"
                                    binding.imgStatus.setImageResource(R.drawable.ic_up)
                                } else {
                                    binding.textStatus.text = "received successfully"
                                    binding.imgStatus.setImageResource(R.drawable.ic_down)
                                }
                                SpinnerLoader.hide()
                            }
                        } else {
                            runOnUiThread {
                                GlobalDialog.showAlert(
                                    this@DetailTransactionActivity,
                                    "Oops..",
                                    apiResponse.message
                                )
                            }
                            SpinnerLoader.hide()
                        }
                        // Proses data sukses
                    }
                } finally {
                    // Sembunyikan spinner loading setelah panggilan API selesai
//                    SpinnerLoader.hide()
                }
            }

            override fun onFailure(call: Call<TransactionResponses>, t: Throwable) {
                // Tangani kesalahan jaringan atau request
                SpinnerLoader.hide()
                if (t is IOException) {
                    // Kegagalan jaringan, misalnya tidak ada koneksi internet
                    GlobalDialog.showAlert(
                        this@DetailTransactionActivity,
                        "Network Error",
                        "Check your internet connection"
                    )
                } else {
                    // Kegagalan lainnya, termasuk kegagalan request ke server
                    GlobalDialog.showAlert(
                        this@DetailTransactionActivity,
                        "Request Error",
                        "Failed to fetch data from server"
                    )
                }
            }
        })
    }
}
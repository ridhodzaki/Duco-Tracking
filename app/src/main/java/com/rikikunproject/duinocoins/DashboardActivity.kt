package com.rikikunproject.duinocoins

import SpinnerLoader
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.rikikunproject.duinocoins.adapter.TransactionAdapter
import com.rikikunproject.duinocoins.components.GlobalDialog
import com.rikikunproject.duinocoins.databinding.ActivityDashboardBinding
import com.rikikunproject.duinocoins.model.Miner
import com.rikikunproject.duinocoins.model.SuccessResponse
import com.rikikunproject.duinocoins.model.Transaction
import com.rikikunproject.duinocoins.network.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class DashboardActivity : AppCompatActivity(), TransactionAdapter.OnItemClickListener  {
    private lateinit var binding: ActivityDashboardBinding
    private val handler = Handler(Looper.getMainLooper())
    private val apiIntervalMillis: Long = 60 * 1000 // Interval 1 menit
    private lateinit var adapter: TransactionAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private var savedUsername = ""
    private var backPressedTime: Long = 0
    private lateinit var toast: Toast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
//        setContentView(R.layout.activity_dashboard)
        setContentView(binding.root)
        supportActionBar?.hide()
        sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
        savedUsername = sharedPreferences.getString("username", null).toString()


        binding.layoutMiners.setOnClickListener {
            var intent = Intent(this, MinersActivity::class.java)
            startActivity(intent)
        }

        binding.btnInfo.setOnClickListener {
            var intent = Intent(this, InfoActivity::class.java)
            startActivity(intent)
        }

        toast = Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT)

        SpinnerLoader.show(this)
        getData()
        scheduleApiCall()

    }

    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            toast.cancel()
            super.onBackPressed()
        } else {
            toast.show()
        }
        backPressedTime = System.currentTimeMillis()
    }

    // Implementasikan metode onItemClick untuk berpindah ke halaman detail
    override fun onItemClick(transaction: Transaction) {
        // Panggil intent untuk membuka halaman detail dan kirim data history ke halaman tersebut
        Log.e(TAG, "${transaction.id}")
        val intent = Intent(this, DetailTransactionActivity::class.java)
        intent.putExtra("id", transaction.id)
        startActivity(intent)
    }

    private fun scheduleApiCall() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                // Panggil fungsi API di sini
                // Contoh: fetchDataFromApi()
                getData()


                // Jadwalkan kembali panggilan setelah interval waktu
                handler.postDelayed(this, apiIntervalMillis)
            }
        }, apiIntervalMillis)
    }

    private fun getData() {
        val call = savedUsername?.let { ApiConfig.apiService.getFullUser(it) }
        call?.enqueue(object : Callback<SuccessResponse> {
            override fun onResponse(call: Call<SuccessResponse>, response: Response<SuccessResponse>) {
                try {
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        Log.e(TAG, response.body().toString())
                        if (apiResponse!!.success) {
                            runOnUiThread {
                                // Code untuk memanggil adapter
                                Log.e(TAG, apiResponse.result.prices.max.toString())
                                binding.textUsername.text = "Hi, ${savedUsername} !"
                                var balance = apiResponse.result.balance.balance.toString()
                                var partsBalance = balance.split(".")
                                if (apiResponse.result.balance.verified !== "yes") {
                                    binding.imgVerified.visibility = View.INVISIBLE
                                }
                                binding.txBalance.text = "${partsBalance[0]}."
                                if (partsBalance[1].length >= 4 ) {
                                    binding.txBalanceComma.text = "${partsBalance[1].substring(0, 4)} DUCO"
                                } else {
                                    binding.txBalanceComma.text = "${partsBalance[1]} DUCO"
                                }
                                binding.textDucoPrices.text = "$ ${apiResponse.result.prices.max}"
                                binding.textMiners.text = apiResponse.result.miners.size.toString()
                                val totalHashrate = calculateTotalHashrate(apiResponse.result.miners)
                                binding.textHashrate.text = "${totalHashrate} kH/s"
                                displayData(apiResponse.result.transactions)
                                SpinnerLoader.hide()
//        // Panggil fungsi untuk menjadwalkan panggilan API setiap 1 menit
                            }
                        } else {
                            runOnUiThread {
                                SpinnerLoader.hide()
                                GlobalDialog.showAlert(
                                    this@DashboardActivity,
                                    "Oops..",
                                    apiResponse.message
                                )
                            }
                        }
                        // Proses data sukses
                    }
                } finally {
                    // Sembunyikan spinner loading setelah panggilan API selesai
//                    SpinnerLoader.hide()
                }
            }

            override fun onFailure(call: Call<SuccessResponse>, t: Throwable) {
                // Tangani kesalahan jaringan atau request
                SpinnerLoader.hide()
                if (t is IOException) {
                    // Kegagalan jaringan, misalnya tidak ada koneksi internet
                    GlobalDialog.showAlert(
                        this@DashboardActivity,
                        "Network Error",
                        "Check your internet connection"
                    )
                } else {
                    // Kegagalan lainnya, termasuk kegagalan request ke server
                    GlobalDialog.showAlert(
                        this@DashboardActivity,
                        "Request Error",
                        "Failed to fetch data from server"
                    )
                }
//              // Panggil fungsi untuk menjadwalkan panggilan API setiap 1 menit
//                scheduleApiCall()
            }
        })
    }

    override fun onDestroy() {
        // Hapus pemanggilan yang masih tertunda saat aktivitas dihancurkan
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    fun calculateTotalHashrate(list: List<Miner>): Double {
        var totalHashrate = 0.0

        for (minerData in list) {
            val hashrate = minerData.hashrate.toDouble()
            totalHashrate += hashrate
        }

        return totalHashrate / 1000.0
    }

    private fun displayData(data: Any) {
        // Mengonversi objek "data" ke jenis yang sesuai
        adapter = TransactionAdapter(data as List<Transaction>, this@DashboardActivity, savedUsername)
        binding.rvTransactions.adapter = adapter
        binding.rvTransactions.layoutManager = LinearLayoutManager(this@DashboardActivity)
        adapter.setOnItemClickListener(this@DashboardActivity)
    }
}
package com.rikikunproject.duinocoins

import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.rikikunproject.duinocoins.adapter.MinersAdapter
import com.rikikunproject.duinocoins.adapter.TransactionAdapter
import com.rikikunproject.duinocoins.components.GlobalDialog
import com.rikikunproject.duinocoins.databinding.ActivityDashboardBinding
import com.rikikunproject.duinocoins.databinding.ActivityMinersBinding
import com.rikikunproject.duinocoins.model.Miner
import com.rikikunproject.duinocoins.model.SuccessResponse
import com.rikikunproject.duinocoins.model.Transaction
import com.rikikunproject.duinocoins.network.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class MinersActivity : AppCompatActivity(), MinersAdapter.OnItemClickListener  {
    private lateinit var binding: ActivityMinersBinding
    private val handler = Handler(Looper.getMainLooper())
    private val apiIntervalMillis: Long = 60 * 1000 // Interval 1 menit
    private lateinit var adapter: MinersAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private var savedUsername = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMinersBinding.inflate(layoutInflater)
//        setContentView(R.layout.activity_miners)
        setContentView(binding.root)
        supportActionBar?.hide()
        sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
        savedUsername = sharedPreferences.getString("username", null).toString()


        binding.btnBack.setOnClickListener {
            this.finish()
        }

        binding.btnInfo.setOnClickListener {
            var intent = Intent(this, InfoActivity::class.java)
            startActivity(intent)
        }

        SpinnerLoader.show(this)
        getData()
        // Panggil fungsi untuk menjadwalkan panggilan API setiap 1 menit
        scheduleApiCall()


    }

    // Implementasikan metode onItemClick untuk berpindah ke halaman detail
    override fun onItemClick(miner: Miner) {
        // Panggil intent untuk membuka halaman detail dan kirim data history ke halaman tersebut
        Log.e(ContentValues.TAG, "${miner.threadid}")
//        val intent = Intent(this, MinersActivity::class.java)
//        intent.putExtra("id", miner.threadid)
//        intent.putExtra("isHitung", false)
//        startActivity(intent)
//        this.finish()
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
                        Log.e(ContentValues.TAG, response.body().toString())
                        if (apiResponse!!.success) {
                            runOnUiThread {
                                // Code untuk memanggil adapter
                                Log.e(ContentValues.TAG, apiResponse.result.prices.max.toString())
                                binding.textUsername.text = "Hi, ${savedUsername} !"
                                val hashrate = calculateTotalHashrate(apiResponse.result.miners).toString()
                                var partshashrate = hashrate.split(".")
                                binding.txHashrate.text = "${partshashrate[0]}."
                                binding.txHashrateComma.text = "${partshashrate[1]} kH/s"
                                displayData(apiResponse.result.miners)
                                SpinnerLoader.hide()
                            }
                        } else {
                            runOnUiThread {
                                GlobalDialog.showAlert(
                                    this@MinersActivity,
                                    "Oops..",
                                    apiResponse.message
                                )
                                SpinnerLoader.hide()
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
                        this@MinersActivity,
                        "Network Error",
                        "Check your internet connection"
                    )
                } else {
                    // Kegagalan lainnya, termasuk kegagalan request ke server
                    GlobalDialog.showAlert(
                        this@MinersActivity,
                        "Request Error",
                        "Failed to fetch data from server"
                    )
                }
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
        adapter = MinersAdapter(data as List<Miner>, this@MinersActivity)
        binding.rvMiners.adapter = adapter
        binding.rvMiners.layoutManager = LinearLayoutManager(this@MinersActivity)
        adapter.setOnItemClickListener(this@MinersActivity)
    }
}
package com.rikikunproject.duinocoins

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rikikunproject.duinocoins.databinding.ActivityInfoBinding

class InfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInfoBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var savedUsername = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
//        setContentView(R.layout.activity_info)
        setContentView(binding.root)
        supportActionBar?.hide()
        sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
        savedUsername = sharedPreferences.getString("username", null).toString()

        binding.textUsername.text = savedUsername

        binding.btnBack.setOnClickListener {
            this.finish()
        }

        binding.buttonLogout.setOnClickListener {
            val sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove("username")
            editor.apply()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }
}
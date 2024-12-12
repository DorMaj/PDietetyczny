package com.example.pdietetyczny

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pdietetyczny.databinding.ActivityJadlospis2Binding

class Jadlospis : AppCompatActivity() {

    private lateinit var binding: ActivityJadlospis2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJadlospis2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomNavigationView.selectedItemId = R.id.jadlospis2


        // DOLNE MENU
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.start -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.opcje -> {
                    startActivity(Intent(this, Opcje::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }

                R.id.bmi -> {
                    startActivity(Intent(this, Bmi::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }


                else -> false
            }
        }

    }
}

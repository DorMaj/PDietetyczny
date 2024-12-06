package com.example.pdietetyczny

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pdietetyczny.databinding.ActivityBmiBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class Bmi : AppCompatActivity() {
    private lateinit var binding: ActivityBmiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBmiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView.selectedItemId = R.id.bmi

        // Listener do BottomNavigationView
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.start -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.bmi -> true
                R.id.opcje -> {
                    startActivity(Intent(this, Opcje::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}
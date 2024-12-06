package com.example.pdietetyczny

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pdietetyczny.databinding.ActivityOpcjeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class Opcje : AppCompatActivity() {
    private lateinit var binding: ActivityOpcjeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOpcjeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView.selectedItemId = R.id.opcje

        // Listener do BottomNavigationView
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.start -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.opcje -> true
                R.id.bmi -> {
                    startActivity(Intent(this, Bmi::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}

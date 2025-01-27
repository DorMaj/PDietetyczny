package com.example.pdietetyczny

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.pdietetyczny.databinding.ActivityJadlospis2Binding

class Jadlospis : AppCompatActivity() {

    private lateinit var binding: ActivityJadlospis2Binding

    // Logika paska i przycisku
    private var currentWater = 0 // Wypita ilość wody w mililitrach
    private val dailyGoal = 1000 // Cel dzienny w mililitrach

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJadlospis2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView.selectedItemId = R.id.jadlospis2

        // Obsługa dolnego menu
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

        // Obsługa przycisku i paska postępu
        val button: Button = findViewById(R.id.add_water_button)
        val progressBar: ProgressBar = findViewById(R.id.water_progress)
        val waterStatus: TextView = findViewById(R.id.water_status)

        progressBar.max = dailyGoal
        progressBar.progress = 0
        updateWaterStatus(waterStatus)

        button.setOnClickListener {
            Log.d("DEBUG", "Przycisk został kliknięty")
            addWater(progressBar, waterStatus)
        }
    }

    private fun addWater(progressBar: ProgressBar, waterStatus: TextView) {
        // Dodajemy 250 ml do obecnego poziomu wody
        if (currentWater < dailyGoal) {
            currentWater += 200
        }

        // Aktualizacja paska postępu
        val progress = currentWater
        progressBar.progress = progress
        Log.d("DEBUG", "Aktualny poziom wody: $currentWater ml")

        // Aktualizacja tekstu
        updateWaterStatus(waterStatus)
    }

    private fun updateWaterStatus(waterStatus: TextView) {
        waterStatus.text = "Wypiłeś $currentWater / $dailyGoal ml"
    }
}

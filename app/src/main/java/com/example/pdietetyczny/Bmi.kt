package com.example.pdietetyczny

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ClipDrawable
import android.os.Bundle
import android.widget.NumberPicker
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat
import com.example.pdietetyczny.databinding.ActivityBmiBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class Bmi : AppCompatActivity() {
    private lateinit var binding: ActivityBmiBinding
    private lateinit var bmiwynik: TextView
    private lateinit var bmistatus: TextView
    private lateinit var bmiprogress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBmiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomNavigationView.selectedItemId = R.id.bmi


        // DOLNE MENU
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.start -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.bmi -> true
                R.id.opcje -> {
                    startActivity(Intent(this, Opcje::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                else -> false
            }
        }
        // Kalkulator BMI
        bmiwynik = findViewById(R.id.wynikbmi)
        bmistatus = findViewById(R.id.statusbmi)
        bmiprogress = findViewById(R.id.progressBar)
        val wzrost = findViewById<NumberPicker>(R.id.wzrost)
        wzrost.minValue = 80
        wzrost.maxValue = 240
        wzrost.value = 160

        val waga = findViewById<NumberPicker>(R.id.waga)
        waga.minValue = 30
        waga.maxValue = 250
        waga.value = 60

        //Nasłuchiwanie zmian w zmiennych
        wzrost.setOnValueChangedListener { NumberPicker, i, i2 ->
            kalkulatorBMI(wzrost.value, waga.value)
        }
        waga.setOnValueChangedListener { NumberPicker, i, i2 ->
            kalkulatorBMI(wzrost.value, waga.value)
        }

    }
    private fun kalkulatorBMI(wzrost: Int, waga: Int){
        //Liczenie bmi
        val bmi = waga/(wzrost/100f*wzrost/100f)
        //Wypisanie wyniku
        bmiwynik.text = "BMI: "+String.format("%.2f", bmi)
        if (bmi<18){
            bmistatus.text = "Niedowaga"
        }else if (bmi<30){
            bmistatus.text = "Zdrowe"
        }else if (bmi<35){
            bmistatus.text = "Nadwaga"
        } else{
            bmistatus.text = "Otyłość"
        }
        //Zmiana wykresu
        bmiprogress.progress = bmi.toInt()
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val layerDrawable = progressBar.progressDrawable as LayerDrawable
        val progressLayer = layerDrawable.findDrawableByLayerId(android.R.id.progress)
        when(bmi.toInt()){
            in 0..17->
                if (progressLayer is ClipDrawable) {
                    val gradientDrawable = progressLayer.drawable as? GradientDrawable
                    gradientDrawable?.setColor(Color.parseColor("#6A9BCC")) // Zmień kolor
                }
            in 18..29 ->
                if (progressLayer is ClipDrawable) {
                    val gradientDrawable = progressLayer.drawable as? GradientDrawable
                    gradientDrawable?.setColor(Color.parseColor("#63B167")) // Zmień kolor
                }
            in 30..34 ->
                if (progressLayer is ClipDrawable) {
                    val gradientDrawable = progressLayer.drawable as? GradientDrawable
                    gradientDrawable?.setColor(Color.parseColor("#CCAE6A")) // Zmień kolor
                }
            in 35..100 ->
                if (progressLayer is ClipDrawable) {
                    val gradientDrawable = progressLayer.drawable as? GradientDrawable
                    gradientDrawable?.setColor(Color.parseColor("#CC796A")) // Zmień kolor
                }
        }
    }
}


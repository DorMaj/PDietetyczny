package com.example.pdietetyczny

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
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
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.opcje -> true
                R.id.bmi -> {
                    startActivity(Intent(this, Bmi::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.jadlospis2 -> {
                    startActivity(Intent(this, Jadlospis::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                else -> false
            }
        }
        val wiek = findViewById<EditText>(R.id.wiek)
        val wzrost = findViewById<EditText>(R.id.wzrost)
        val waga = findViewById<EditText>(R.id.waga)
        val plec = findViewById<RadioGroup>(R.id.radioGroupGender)
        val aktywnosc = findViewById<Spinner>(R.id.aktywnosc)
        val oblicz = findViewById<Button>(R.id.oblicz)
        val wyczysc = findViewById<Button>(R.id.wyczysc)
        val wynik = findViewById<TextView>(R.id.wynik)


        val wzory = arrayOf(1.375, 1.55, 1.725)
        val aktywnoscWyb = arrayOf(
            "Niska aktywnosc (15-30min)",
            "Średnia aktywnosc (45-120min)",
            "Wysoka aktywnosc (2g+)",
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, aktywnoscWyb)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        aktywnosc.adapter = adapter

        // Przycisk Oblicz
        oblicz.setOnClickListener {
            val DaneWiek = wiek.text.toString().toIntOrNull()
            val DaneWzrost = wzrost.text.toString().toDoubleOrNull()
            val DaneWaga = waga.text.toString().toDoubleOrNull()
            val Daneplec = plec.checkedRadioButtonId
            val WybPlec = if (Daneplec == R.id.radioMale) "male" else "female"
            val WybAktywnosc = aktywnosc.selectedItemPosition
            val WzorWyboru = wzory[WybAktywnosc]

            if (wiek != null && wzrost != null && waga != null) {
                val bmr = if (WybPlec == "male") {
                    10 * DaneWaga!! + 6.25 * DaneWzrost!! - 5 * DaneWiek!! + 5
                } else {
                    10 * DaneWaga!! + 6.25 * DaneWzrost!! - 5 * DaneWiek!! - 161
                }

                // Obliczenie TDEE
                val tdee = bmr * WzorWyboru

                // Dostosowanie kalorii do wyboru aktywnosci
                val utrzymac = tdee
                val niskie = tdee - 250
                val srednie = tdee - 500
                val wysokie = tdee - 1000

                // Display Results
                wynik.text =
                    """
                    Utrzymanie wagi: ${utrzymac.toInt()} Kalorii dziennie
                    Niski spadek wagi: ${niskie.toInt()} Kalorii dziennie
                    Średni spadek wagi: ${srednie.toInt()} Kalorii dziennie
                    Wysoki spadek wagi: ${wysokie.toInt()} Kalorii dziennie
                    """.trimIndent()
            }else{



            }
        }

        // Wyczysc
        wyczysc.setOnClickListener {
            wiek.text.clear()
            wzrost.text.clear()
            waga.text.clear()
            plec.check(R.id.radioMale)
            aktywnosc.setSelection(0)
            wynik.text =
                                "Niska aktywność: 15-30 minut wysiłku fizycznego.\n" +
                                "Średnia aktywnosc: 45-120 minut aktywnosci fizycznej.\n" +
                                "Wysoka aktywnosc: Powyżej 2 godzin aktywnosci fizycznej."
        }
    }
}

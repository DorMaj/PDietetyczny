package com.example.pdietetyczny

import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.pdietetyczny.R.layout.activity_main
import com.example.pdietetyczny.database.DatabaseHelper
import com.example.pdietetyczny.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import com.example.pdietetyczny.models.FoodItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var foodList: List<FoodItem> // Lista produktów

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activity_main)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { item ->
            if (item.itemId == bottomNavigationView.selectedItemId) {
                return@setOnItemSelectedListener false
            }

            when (item.itemId) {

                R.id.bmi -> {
                    startActivity(Intent(this, Bmi::class.java))
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.opcje -> {
                    startActivity(Intent(this, Opcje::class.java))
                    overridePendingTransition(0, 0)
                    true
                }

                else -> false
            }

        }

        foodList = loadJsonFromAssets()

        // Zainicjalizowanie AutoCompleteTextView
        val productNames = foodList.map { it.name }
        val searchProduct = findViewById<AutoCompleteTextView>(R.id.searchProduct)

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            productNames
        )
        searchProduct.setAdapter(adapter)

        // Obsługa kliknięcia na proponowaną nazwę produktu
        searchProduct.setOnItemClickListener { parent, view, position, id ->
            val selectedName = parent.getItemAtPosition(position).toString()
            val selectedProduct = foodList.find { it.name == selectedName }
            selectedProduct?.let {
                findViewById<TextView>(R.id.resultTextView).text = """
                    Nazwa: ${it.name}
                    Kalorie: ${it.calories}
                    Białko: ${it.protein}
                    Cukier: ${it.sugar}
                    Tłuszcz: ${it.fat}
                """.trimIndent()
            }
        }
    }
    // Funkcja do ładowania danych z pliku JSON
    private fun loadJsonFromAssets(): List<FoodItem> {
        val jsonString = assets.open("Food_database.json").bufferedReader().use { it.readText() }
        val gson = Gson()
        return gson.fromJson(jsonString, Array<FoodItem>::class.java).toList()
    }
}






package com.example.pdietetyczny

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.pdietetyczny.databinding.ActivityMainBinding
import com.example.pdietetyczny.models.FoodItem
import com.google.gson.Gson
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    // Lista produktów, która będzie dostępna globalnie w aplikacji
    companion object {
        lateinit var foodList: List<FoodItem>
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)  // Używamy binding.root

        val bottomNavigationView: BottomNavigationView = binding.bottomNavigationView  // Odwołanie do bindingu
        bottomNavigationView.setOnItemSelectedListener { item ->
            if (item.itemId == bottomNavigationView.selectedItemId) {
                return@setOnItemSelectedListener false
            }

            when (item.itemId) {
                R.id.bmi -> {
                    startActivity(Intent(this, Bmi::class.java))
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
                R.id.jadlospis2 -> {
                    // Przekazanie foodList do JadlospisNew za pomocą Intent
                    val intent = Intent(this, Jadlospis::class.java)

                    // Konwersja foodList na JSON
                    val gson = Gson()
                    val json = gson.toJson(foodList)

                    // Dodanie JSON do Intentu
                    intent.putExtra("FOOD_LIST", json)

                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                else -> false
            }
        }

        // Ładowanie listy produktów z pliku JSON
        foodList = loadJsonFromAssets()

        // Zainicjalizowanie AutoCompleteTextView z listą produktów
        val productNames = foodList.map { it.name }
        val searchProduct = binding.searchProduct  // Z bindingu

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            productNames
        )
        searchProduct.setAdapter(adapter)

        // Obsługa kliknięcia na proponowaną nazwę produktu
        searchProduct.setOnItemClickListener { parent, _, position, _ ->
            val selectedName = parent.getItemAtPosition(position).toString()
            val selectedProduct = foodList.find { it.name == selectedName }
            selectedProduct?.let {
                binding.resultTextView.text = """
                    ${it.name}
                    Kcal: ${it.calories}
                    Białko: ${it.protein}
                    Węglowodany: ${it.sugar}
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

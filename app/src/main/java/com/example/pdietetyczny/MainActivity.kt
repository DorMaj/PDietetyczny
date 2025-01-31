package com.example.pdietetyczny

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.pdietetyczny.databinding.ActivityMainBinding
import com.example.pdietetyczny.models.FoodItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        var foodList: List<FoodItem> = emptyList()
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicjalizacja SharedPreferences
        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)

        // Wczytaj listę produktów
        foodList = loadFoodListFromPreferences()

        if (foodList.isEmpty()) {
            foodList = loadJsonFromAssets()
            saveFoodListToPreferences()
        }

        val bottomNavigationView: BottomNavigationView = binding.bottomNavigationView
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
                    val intent = Intent(this, Jadlospis::class.java)
                    val gson = Gson()
                    val json = gson.toJson(foodList)
                    intent.putExtra("FOOD_LIST", json)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                else -> false
            }
        }

        // Inicjalizacja AutoCompleteTextView z listą produktów
        val productNames = foodList.map { it.name }
        val searchProduct = binding.searchProduct

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

    // Funkcja do zapisu listy do SharedPreferences
    private fun saveFoodListToPreferences() {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(foodList)
        editor.putString("FOOD_LIST", json)
        editor.apply()
    }

    // Funkcja do wczytywania listy z SharedPreferences
    private fun loadFoodListFromPreferences(): List<FoodItem> {
        val json = sharedPreferences.getString("FOOD_LIST", null)
        return if (json != null) {
            val type = object : TypeToken<List<FoodItem>>() {}.type
            Gson().fromJson(json, type)
        } else {
            emptyList()
        }
    }
}

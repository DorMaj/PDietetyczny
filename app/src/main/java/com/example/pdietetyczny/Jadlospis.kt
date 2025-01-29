package com.example.pdietetyczny

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pdietetyczny.models.FoodItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.pdietetyczny.databinding.ActivityJadlospis2Binding
import android.content.Intent

import android.widget.*

class Jadlospis : AppCompatActivity() {

    private lateinit var binding: ActivityJadlospis2Binding

    // Lista produktów
    private lateinit var foodList: List<FoodItem>

    // Kategorie posiłków
    private val breakfastItems = mutableListOf<FoodItem>()

    // Ilość wody
    private var waterIntake = 0
    private val maxWaterIntake = 2000

    // Waga użytkownika
    private var userWeight: Float = 70f // Domyślna waga

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJadlospis2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obsługa dolnego menu
        binding.bottomNavigationView.selectedItemId = R.id.jadlospis2
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

        // Odbierz dane z Intentu
        val json = intent.getStringExtra("FOOD_LIST")
        if (json != null) {
            val gson = Gson()
            val type = object : TypeToken<List<FoodItem>>() {}.type
            foodList = gson.fromJson(json, type)
        }

        // Przypisanie funkcji do przycisków
        setupMealButtons()

        // Inicjalizacja progress barów
        updateProgressBars()
        updateWaterProgressBar()

        // Obsługa wagi użytkownika
        setupWeightInput()
    }

    private fun setupWeightInput() {
        binding.setWeightButton.setOnClickListener {
            val enteredWeight = binding.weightInput.text.toString().toFloatOrNull()

            if (enteredWeight == null || enteredWeight < 30f || enteredWeight > 200f) {
                Toast.makeText(this, "Podaj poprawną wagę (30-200 kg)", Toast.LENGTH_SHORT).show()
            } else {
                userWeight = enteredWeight
                updateMacrosBasedOnWeight()
            }
        }
    }

    private fun updateMacrosBasedOnWeight() {
        val dailyCalories = userWeight * 30
        val dailyProtein = userWeight * 1.8f
        val dailyCarbs = userWeight * 4f
        val dailyFat = userWeight * 1f

        binding.proteinProgress.max = dailyProtein.toInt()
        binding.carbsProgress.max = dailyCarbs.toInt()
        binding.fatProgress.max = dailyFat.toInt()
        binding.kalorieProgress.max = dailyCalories.toInt()

        Toast.makeText(this, "Zaktualizowano makro dla ${userWeight} kg!", Toast.LENGTH_SHORT).show()
    }

    private fun setupMealButtons() {
        binding.addPosilekButton.setOnClickListener {
            openProductSelectionDialog { selectedProduct ->
                addMealToCategory(breakfastItems, selectedProduct)
                Toast.makeText(this, "Posiłek dodany!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.addWaterButton.setOnClickListener {
            if (waterIntake + 200 <= maxWaterIntake) {
                waterIntake += 200
                updateWaterProgressBar()
                Toast.makeText(this, "Dodano 200 ml wody!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Maksymalna ilość wody to 2000 ml!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addMealToCategory(category: MutableList<FoodItem>, item: FoodItem) {
        category.add(item)
        updateProgressBars()
    }

    private fun updateProgressBars() {
        val totalProtein = calculateTotal { it.protein }
        val totalCarbs = calculateTotal { it.sugar }
        val totalFat = calculateTotal { it.fat }
        val totalCalories = calculateTotal { it.calories }

        binding.proteinProgress.progress = totalProtein.toInt()
        binding.carbsProgress.progress = totalCarbs.toInt()
        binding.fatProgress.progress = totalFat.toInt()
        binding.kalorieProgress.progress = totalCalories.toInt()
    }

    private fun calculateTotal(selector: (FoodItem) -> Float): Float {
        return (breakfastItems)
            .sumByDouble { selector(it).toDouble() }.toFloat()
    }

    private fun updateWaterProgressBar() {
        val progress = (waterIntake.toFloat() / maxWaterIntake) * 100
        binding.waterProgress.progress = progress.toInt()
        binding.waterStatus.text = "$waterIntake / $maxWaterIntake ml"
    }
    private fun openWeightInputDialog(selectedProduct: FoodItem, onProductSelected: (FoodItem) -> Unit) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Podaj gramaturę dla: ${selectedProduct.name}")

        val input = EditText(this)
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        input.hint = "Wpisz wagę w gramach"

        builder.setView(input)

        builder.setPositiveButton("Dodaj") { _, _ ->
            val weight = input.text.toString().toFloatOrNull()

            if (weight == null || weight <= 0) {
                Toast.makeText(this, "Wpisz poprawną wagę!", Toast.LENGTH_SHORT).show()
            } else {
                // Przeliczanie wartości odżywczych proporcjonalnie do podanej gramatury
                val scaleFactor = weight / 100f

                val adjustedProduct = FoodItem(
                    id = selectedProduct.id,
                    name = "${selectedProduct.name} (${weight}g)", // Dodajemy gramaturę do nazwy
                    calories = selectedProduct.calories * scaleFactor,
                    protein = selectedProduct.protein * scaleFactor,
                    sugar = selectedProduct.sugar * scaleFactor,
                    fat = selectedProduct.fat * scaleFactor
                )

                onProductSelected(adjustedProduct)
            }
        }

        builder.setNegativeButton("Anuluj") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    private fun openProductSelectionDialog(onProductSelected: (FoodItem) -> Unit) {
        val productNames = foodList.map { it.name }.toMutableList()

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Wybierz produkt")

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        val searchView = SearchView(this)
        layout.addView(searchView)

        val listView = ListView(this)
        layout.addView(listView)

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, productNames)
        listView.adapter = adapter

        builder.setView(layout)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    val filtered = foodList.filter { it.name.contains(newText, ignoreCase = true) }
                    adapter.clear()
                    adapter.addAll(filtered.map { it.name })
                    adapter.notifyDataSetChanged()
                }
                return true
            }
        })

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedProduct = foodList.first { it.name == adapter.getItem(position) }
            openWeightInputDialog(selectedProduct, onProductSelected)
        }

        builder.setNegativeButton("Zamknij") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}

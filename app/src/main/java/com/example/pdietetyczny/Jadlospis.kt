package com.example.pdietetyczny

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pdietetyczny.models.FoodItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.pdietetyczny.databinding.ActivityJadlospis2Binding
import android.content.Intent
import android.content.SharedPreferences

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

    private lateinit var mealAdapter: MealAdapter

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJadlospis2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ustawienie SharedPreferences
        sharedPreferences = getSharedPreferences("USER_DATA", MODE_PRIVATE)

        // Załaduj dane z SharedPreferences
        loadUserData()

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

        // Inicjalizacja RecyclerView
        mealAdapter = MealAdapter(breakfastItems)
        binding.mealRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.mealRecyclerView.adapter = mealAdapter

        // Dodanie nagłówka listy
        binding.mealListTitle.text = "Lista dodanych rzeczy:"
    }

    private fun setupWeightInput() {
        binding.setWeightButton.setOnClickListener {
            val enteredWeight = binding.weightInput.text.toString().toFloatOrNull()

            if (enteredWeight == null || enteredWeight < 30f || enteredWeight > 200f) {
                Toast.makeText(this, "Podaj poprawną wagę (30-200 kg)", Toast.LENGTH_SHORT).show()
            } else {
                userWeight = enteredWeight
                updateMacrosBasedOnWeight()
                saveUserData()
            }
        }
    }

    private fun saveUserData() {
        val editor = sharedPreferences.edit()
        editor.putFloat("userWeight", userWeight)
        editor.putInt("waterIntake", waterIntake)
        editor.putString("breakfastItems", Gson().toJson(breakfastItems))
        editor.apply()
    }

    private fun loadUserData() {
        userWeight = sharedPreferences.getFloat("userWeight", 70f)
        waterIntake = sharedPreferences.getInt("waterIntake", 0)
        val breakfastItemsJson = sharedPreferences.getString("breakfastItems", "[]")
        if (breakfastItemsJson != null) {
            val gson = Gson()
            val type = object : TypeToken<List<FoodItem>>() {}.type
            breakfastItems.clear()
            breakfastItems.addAll(gson.fromJson(breakfastItemsJson, type))
        }
    }

    private fun updateMacrosBasedOnWeight() {
        val dailyCalories = userWeight * 30
        val dailyProtein = userWeight * 1.8f
        val dailyCarbs = userWeight * 4f
        val dailyFat = userWeight * 1f

        // Zaktualizowanie maksymalnych wartości ProgressBarów
        binding.proteinProgress.max = dailyProtein.toInt()
        binding.carbsProgress.max = dailyCarbs.toInt()
        binding.fatProgress.max = dailyFat.toInt()
        binding.kalorieProgress.max = dailyCalories.toInt()

        // Zaktualizowanie tekstu informującego o nowych wartościach
        binding.proteinLabel.text = "Białka: 0 / ${dailyProtein.toInt()} g"
        binding.carbsLabel.text = "Węglowodany: 0 / ${dailyCarbs.toInt()} g"
        binding.fatLabel.text = "Tłuszcze: 0 / ${dailyFat.toInt()} g"
        binding.kalorieLabel.text = "Kalorie: 0 / ${dailyCalories.toInt()} kcal"

        // Aktualizacja makroskładników dla dodanych posiłków
        breakfastItems.forEach { meal ->
            val scaleFactor =
                meal.calories / 100f // Przykład dla kalorii, przeliczyć na podstawie wagi
            meal.calories = meal.calories * scaleFactor // Przypisanie do var
            meal.protein = meal.protein * scaleFactor   // Przypisanie do var
            meal.sugar = meal.sugar * scaleFactor       // Przypisanie do var
            meal.fat = meal.fat * scaleFactor           // Przypisanie do var
        }

        updateProgressBars() // Zaktualizuj progresy po zmianie wagi
        Toast.makeText(this, "Zaktualizowano makro dla ${userWeight} kg!", Toast.LENGTH_SHORT)
            .show()
    }


    private fun updateProgressBar() {
        val totalProtein = calculateTotal { it.protein }
        val totalCarbs = calculateTotal { it.sugar }
        val totalFat = calculateTotal { it.fat }
        val totalCalories = calculateTotal { it.calories }

        // Ustawienie progres barów
        binding.proteinProgress.progress = totalProtein.toInt()
        binding.carbsProgress.progress = totalCarbs.toInt()
        binding.fatProgress.progress = totalFat.toInt()
        binding.kalorieProgress.progress = totalCalories.toInt()

        // Zaktualizowanie etykiet z makro wartościami
        binding.proteinLabel.text =
            "Białka: ${totalProtein.toInt()} / ${binding.proteinProgress.max} g"
        binding.carbsLabel.text =
            "Węglowodany: ${totalCarbs.toInt()} / ${binding.carbsProgress.max} g"
        binding.fatLabel.text = "Tłuszcze: ${totalFat.toInt()} / ${binding.fatProgress.max} g"
        binding.kalorieLabel.text =
            "Kalorie: ${totalCalories.toInt()} / ${binding.kalorieProgress.max} kcal"
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

        binding.resetButton.setOnClickListener {
            resetData()
        }
    }

    private fun resetData() {
        // Resetowanie wprowadzonych danych
        breakfastItems.clear()  // Usunięcie wszystkich posiłków z listy
        mealAdapter.notifyDataSetChanged()  // Powiadomienie adaptera o zmianach

        // Resetowanie wartości progress barów
        waterIntake = 0
        updateProgressBars()
        updateWaterProgressBar()

        // Resetowanie wagi użytkownika
        userWeight = 70f
        binding.weightInput.setText(userWeight.toString())

        // Resetowanie makroskładników
        binding.proteinLabel.text = "Białka: 0 / ${binding.proteinProgress.max} g"
        binding.carbsLabel.text = "Węglowodany: 0 / ${binding.carbsProgress.max} g"
        binding.fatLabel.text = "Tłuszcze: 0 / ${binding.fatProgress.max} g"
        binding.kalorieLabel.text = "Kalorie: 0 / ${binding.kalorieProgress.max} kcal"
    }

    private fun updateProgressBars() {
        val totalProtein = calculateTotal { it.protein }
        val totalCarbs = calculateTotal { it.sugar }
        val totalFat = calculateTotal { it.fat }
        val totalCalories = calculateTotal { it.calories }

        // Ustawienie progres barów
        binding.proteinProgress.progress = totalProtein.toInt()
        binding.carbsProgress.progress = totalCarbs.toInt()
        binding.fatProgress.progress = totalFat.toInt()
        binding.kalorieProgress.progress = totalCalories.toInt()

        // Zaktualizowanie etykiet z makro wartościami
        binding.proteinLabel.text =
            "Białka: ${totalProtein.toInt()} / ${binding.proteinProgress.max} g"
        binding.carbsLabel.text =
            "Węglowodany: ${totalCarbs.toInt()} / ${binding.carbsProgress.max} g"
        binding.fatLabel.text = "Tłuszcze: ${totalFat.toInt()} / ${binding.fatProgress.max} g"
        binding.kalorieLabel.text =
            "Kalorie: ${totalCalories.toInt()} / ${binding.kalorieProgress.max} kcal"
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

    private fun openWeightInputDialog(
        selectedProduct: FoodItem,
        onProductSelected: (FoodItem) -> Unit
    ) {
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
        saveUserData()  //
    }

    private fun addMealToCategory(category: MutableList<FoodItem>, item: FoodItem) {
        category.add(item)
        updateProgressBars()
        mealAdapter.notifyDataSetChanged()
    }
}




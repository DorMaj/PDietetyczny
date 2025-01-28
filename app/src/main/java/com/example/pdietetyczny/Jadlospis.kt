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

class Jadlospis : AppCompatActivity() {

    // Inicjalizujemy listę foodList jako zmienną globalną
    private lateinit var foodList: List<FoodItem>

    // Kategoria produktów
    private val breakfastItems = mutableListOf<FoodItem>()
    private val lunchItems = mutableListOf<FoodItem>()
    private val dinnerItems = mutableListOf<FoodItem>()
    private val dessertItems = mutableListOf<FoodItem>()
    private val supperItems = mutableListOf<FoodItem>()

    // Zmienna do przechowywania ilości wypitej wody
    private var waterIntake = 0 // Ilość wypitej wody w mililitrach
    private val maxWaterIntake = 2000 // Maksymalna ilość wody do wypicia

    // Binding
    private lateinit var binding: ActivityJadlospis2Binding

    private var filteredProductList = listOf<FoodItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJadlospis2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ustawienie wybranego elementu w dolnym menu
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

        // Odbierz dane z Intentu
        val json = intent.getStringExtra("FOOD_LIST")
        if (json != null) {
            val gson = Gson()
            val type = object : TypeToken<List<FoodItem>>() {}.type
            foodList = gson.fromJson(json, type)
        }

        // Przypisanie funkcji do przycisków
        setupMealButtons()

        // Aktualizacja progress barów na starcie
        updateProgressBars()

        // Inicjalizacja progress bara wody
        updateWaterProgressBar()
    }

    // Funkcja przypisująca kliknięcia do przycisków
    private fun setupMealButtons() {
        binding.addSniadanieButton.setOnClickListener {
            openProductSelectionDialog { selectedProduct ->
                addMealToCategory(breakfastItems, selectedProduct)
                Toast.makeText(this, "Śniadanie dodane!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.addDrugieSniadanieButton.setOnClickListener {
            openProductSelectionDialog { selectedProduct ->
                addMealToCategory(lunchItems, selectedProduct)
                Toast.makeText(this, "Drugie śniadanie dodane!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.addObiadButton.setOnClickListener {
            openProductSelectionDialog { selectedProduct ->
                addMealToCategory(dinnerItems, selectedProduct)
                Toast.makeText(this, "Obiad dodany!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.addDeserButton.setOnClickListener {
            openProductSelectionDialog { selectedProduct ->
                addMealToCategory(dessertItems, selectedProduct)
                Toast.makeText(this, "Deser dodany!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.addKolacjaButton.setOnClickListener {
            openProductSelectionDialog { selectedProduct ->
                addMealToCategory(supperItems, selectedProduct)
                Toast.makeText(this, "Kolacja dodana!", Toast.LENGTH_SHORT).show()
            }
        }

        // Dodanie przycisku do dodawania wody
        binding.addWaterButton.setOnClickListener {
            // Sprawdzamy, czy można dodać więcej wody
            if (waterIntake + 200 <= maxWaterIntake) {
                waterIntake += 200
                updateWaterProgressBar()
                Toast.makeText(this, "Dodano 200 ml wody!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Maksymalna ilość wody to 2000 ml!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Funkcja do dodawania posiłku do danej kategorii (np. deser)
    private fun addMealToCategory(category: MutableList<FoodItem>, item: FoodItem) {
        category.add(item)
        updateProgressBars()
    }

    // Funkcja aktualizująca paska postępu dla białka, węglowodanów, tłuszczy i kalorii
    private fun updateProgressBars() {
        val totalProtein = calculateTotal { it.protein }
        val totalCarbs = calculateTotal { it.sugar }
        val totalFat = calculateTotal { it.fat }
        val totalCalories = calculateTotal { it.calories }

        // Aktualizacja wartości progress barów (konwersja Float na Int)
        binding.proteinProgress.progress = totalProtein.toInt()
        binding.carbsProgress.progress = totalCarbs.toInt()
        binding.fatProgress.progress = totalFat.toInt()
        binding.kalorieProgress.progress = totalCalories.toInt()
    }

    // Funkcja obliczająca sumę wartości dla danej kategorii (np. białka, węglowodanów)
    private fun calculateTotal(selector: (FoodItem) -> Float): Float {
        return (breakfastItems + lunchItems + dinnerItems + dessertItems + supperItems)
            .sumByDouble { selector(it).toDouble() }.toFloat() // Konwertujemy na Double, a potem z powrotem na Float
    }

    // Funkcja do otwierania dialogu wyboru produktu
    private fun openProductSelectionDialog(onProductSelected: (FoodItem) -> Unit) {
        val productNames = foodList.map { it.name }

        // Tworzymy adapter do wyświetlania listy produktów
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, productNames)

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Wybierz produkt")

        // Dodajemy pole do wyszukiwania
        val searchView = android.widget.SearchView(this)

        // Dodajemy EditText do ręcznego wpisania produktu
        val editText = android.widget.EditText(this)
        editText.hint = "Wpisz nazwę produktu"

        val layout = android.widget.LinearLayout(this)
        layout.orientation = android.widget.LinearLayout.VERTICAL
        layout.addView(searchView)
        layout.addView(editText)

        builder.setView(layout)

        // Zmienna do przechowywania przefiltrowanych produktów
        var filteredProductNames = productNames

        // Reakcja na zmianę tekstu w polu wyszukiwania
        searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    // Filtrowanie listy produktów na podstawie tekstu
                    filteredProductNames = productNames.filter { it.contains(newText, ignoreCase = true) }

                    // Dynamiczna aktualizacja adaptera z przefiltrowaną listą
                    adapter.clear()
                    adapter.addAll(filteredProductNames)
                    adapter.notifyDataSetChanged()
                }
                return true
            }
        })

        builder.setAdapter(adapter) { _, which ->
            val selectedProduct = foodList.first { it.name == filteredProductNames[which] }
            onProductSelected(selectedProduct)
        }

        builder.setPositiveButton("Dodaj ręcznie") { dialog, _ ->
            val productName = editText.text.toString().trim()

            if (productName.isNotEmpty()) {
                // Tworzymy nowy produkt z id = 0 oraz pozostałymi domyślnymi wartościami
                val newProduct = FoodItem(
                    id = 0,  // Dodajemy id (np. 0 lub generowanie ID)
                    name = productName,
                    calories = 0f,
                    protein = 0f,
                    sugar = 0f,
                    fat = 0f
                )
                onProductSelected(newProduct)
                Toast.makeText(this, "$productName dodano do listy!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Proszę wpisać nazwę produktu!", Toast.LENGTH_SHORT).show()
            }

            dialog.dismiss()
        }

        builder.setCancelable(true)
        builder.setNegativeButton("Zamknij") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    // Funkcja do aktualizacji progress bara wody
    private fun updateWaterProgressBar() {
        // Obliczamy procent postępu
        val progress = (waterIntake.toFloat() / maxWaterIntake) * 100

        // Zaktualizowanie progress bara dla wody
        binding.waterProgress.progress = progress.toInt()

        // Zaktualizowanie tekstu pokazującego aktualny stan
        binding.waterStatus.text = "$waterIntake / $maxWaterIntake ml"
    }

    // Funkcja obsługująca przycisk wstecz
    override fun onBackPressed() {
        super.onBackPressed() // Zapewnia, że aplikacja wróci do poprzedniej aktywności
    }
}

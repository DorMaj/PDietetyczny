package com.example.pdietetyczny

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pdietetyczny.models.FoodItem

class MealAdapter(private var mealList: MutableList<FoodItem>) :
    RecyclerView.Adapter<MealAdapter.MealViewHolder>() {

    // Zmieniamy mealList na MutableList, aby można było ją modyfikować

    // Dodajemy metodę do aktualizacji danych w adapterze
    fun updateData(newMealList: List<FoodItem>) {
        mealList.clear()           // Usuwamy poprzednie dane
        mealList.addAll(newMealList)  // Dodajemy nowe dane
        notifyDataSetChanged()     // Powiadamiamy adapter, że dane się zmieniły
    }

    class MealViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mealName: TextView = view.findViewById(R.id.mealName)
        val mealDetails: TextView = view.findViewById(R.id.mealDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_meal, parent, false)
        return MealViewHolder(view)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val meal = mealList[position]
        holder.mealName.text = meal.name
        holder.mealDetails.text =
            "Kalorie: ${meal.calories.toInt()} kcal, Białko: ${meal.protein.toInt()} g, Węglowodany: ${meal.sugar.toInt()} g, Tłuszcz: ${meal.fat.toInt()} g"
    }

    override fun getItemCount() = mealList.size
}

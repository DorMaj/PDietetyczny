package com.example.pdietetyczny.models

data class FoodItem(
    val id: Int,
    var name: String,
    var calories: Float,
    var protein: Float,
    var sugar: Float,
    var fat: Float,

)
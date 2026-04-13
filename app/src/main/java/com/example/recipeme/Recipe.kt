package com.example.recipeme


class Recipe(
    val label: String,              
    val image: String,
    val source: String,
    val url: String,
    val calories: Double,
    val ingredientLines: List<String>
)
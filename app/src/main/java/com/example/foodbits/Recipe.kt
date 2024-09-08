package com.example.foodbits

enum class Visibility {
    PUBLIC,
    PRIVATE
}

data class Recipe (
    val name: String = "",
    val description: String = "",
    val ingredients: List<String> = emptyList(),
    val steps: List<String> = emptyList(),
    val imageUrl: String = "",
    val visibility: Visibility = Visibility.PUBLIC,
    val userId: String = ""
)


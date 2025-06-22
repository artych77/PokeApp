package com.example.PokeApp.presentation.regions_list

fun getGenerationForRegion(regionName: String): String {
    return when (regionName.lowercase()) {
        "kanto" -> "1ª GERAÇÃO"
        "johto" -> "2ª GERAÇÃO"
        "hoenn" -> "3ª GERAÇÃO"
        "sinnoh" -> "4ª GERAÇÃO"
        "unova" -> "5ª GERAÇÃO"
        "kalos" -> "6ª GERAÇÃO"
        "alola" -> "7ª GERAÇÃO"
        "galar" -> "8ª GERAÇÃO"
        else -> "-"
    }
}

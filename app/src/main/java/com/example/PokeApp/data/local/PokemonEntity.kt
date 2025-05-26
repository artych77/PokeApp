package com.example.PokeApp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon_table")
data class PokemonEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String,
    val types: String,
    val region: String,
    val weight: Float,
    val height: Float,
    val category: String,
    val ability: String,
    val description: String,
    val weaknesses: String,
    val evolutions: String,
    val isFavorite: Boolean = false
)

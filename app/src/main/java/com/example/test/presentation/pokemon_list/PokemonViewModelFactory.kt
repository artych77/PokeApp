package com.example.PokeApp.presentation.pokemon_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.PokeApp.data.repository.PokemonRepository

class PokemonViewModelFactory(
    private val repository: PokemonRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PokemonViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PokemonViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

package com.example.PokeApp.presentation.regions_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.PokeApp.data.repository.PokemonRepository

class RegionsViewModelFactory(
    private val repository: PokemonRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegionsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegionsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

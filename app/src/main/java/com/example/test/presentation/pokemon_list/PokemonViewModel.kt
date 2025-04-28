package com.example.PokeApp.presentation.pokemon_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.PokeApp.data.local.PokemonEntity
import com.example.PokeApp.data.repository.PokemonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PokemonViewModel(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _pokemonList = MutableStateFlow<List<PokemonEntity>>(emptyList())
    val pokemonList: StateFlow<List<PokemonEntity>> = _pokemonList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadPokemonList()
    }

    fun loadPokemonList() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repository.fetchPokemonList()
                _pokemonList.value = repository.getAllPokemon.first()
            } catch (e: Exception) {
                _errorMessage.value = "Błąd ładowania danych: ${e.localizedMessage ?: "Nieznany błąd"}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

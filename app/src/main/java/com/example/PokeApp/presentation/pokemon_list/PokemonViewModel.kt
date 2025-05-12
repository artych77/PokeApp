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
import kotlinx.coroutines.flow.combine


class PokemonViewModel(
    private val repository: PokemonRepository
) : ViewModel() {

    val pokemonList: StateFlow<List<PokemonEntity>> =
        repository.getAllPokemon
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    val filteredPokemonList: StateFlow<List<PokemonEntity>> =
        searchQuery.combine(pokemonList) { query, list ->
            if (query.isBlank()) list
            else list.filter { it.name.contains(query.trim(), ignoreCase = true) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadPokemonList()
    }

    fun loadPokemonList() {
        viewModelScope.launch {
            repository.fetchPokemonListIfNeeded()
        }
    }
}

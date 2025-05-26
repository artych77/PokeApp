package com.example.PokeApp.presentation.pokemon_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.PokeApp.data.local.PokemonEntity
import com.example.PokeApp.data.network.PokemonDetailResponse
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

    private val _selectedSort = MutableStateFlow("Smallest number")
    val selectedSort: StateFlow<String> = _selectedSort

    val sortOptions = listOf("Smallest number", "Largest number", "A–Z", "Z–A")

    fun onSelectedSortChange(option: String) {
        _selectedSort.value = option
    }
    private val _selectedType = MutableStateFlow("All types")
    val selectedType: StateFlow<String> = _selectedType

    fun onSelectedTypeChange(type: String) {
        _selectedType.value = type
    }

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
        combine(pokemonList, searchQuery, selectedType, selectedSort) { list, query, type, sort ->
            list
                .filter { pokemon ->
                    val matchesQuery = pokemon.name.contains(query.trim(), ignoreCase = true)
                    val matchesType = type == "All types" || pokemon.types.contains(type, ignoreCase = true)
                    matchesQuery && matchesType
                }
                .let { filtered ->
                    when (sort) {
                        "A–Z" -> filtered.sortedBy { it.name }
                        "Z–A" -> filtered.sortedByDescending { it.name }
                        "Smallest number" -> filtered.sortedBy { it.id }
                        "Largest number" -> filtered.sortedByDescending { it.id }
                        else -> filtered
                    }
                }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadPokemonList()
    }

    fun loadPokemonList() {
        viewModelScope.launch {
            repository.fetchPokemonListIfNeeded()
        }
    }

    fun toggleFavorite(pokemon: PokemonEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(pokemon)
        }
    }
    private val _favoriteSearchQuery = MutableStateFlow("")
    val favoriteSearchQuery: StateFlow<String> = _favoriteSearchQuery

    fun onFavoriteSearchQueryChange(query: String) {
        _favoriteSearchQuery.value = query
    }

    val filteredFavorites: StateFlow<List<PokemonEntity>> =
        combine(pokemonList, searchQuery, selectedType, selectedSort) { list, query, type, sort ->
            list
                .filter { it.isFavorite }
                .filter { pokemon ->
                    val matchesQuery = pokemon.name.contains(query.trim(), ignoreCase = true)
                    val matchesType = type == "All types" || pokemon.types.contains(type, ignoreCase = true)
                    matchesQuery && matchesType
                }
                .let { filtered ->
                    when (sort) {
                        "A–Z" -> filtered.sortedBy { it.name }
                        "Z–A" -> filtered.sortedByDescending { it.name }
                        "Smallest number" -> filtered.sortedBy { it.id }
                        "Largest number" -> filtered.sortedByDescending { it.id }
                        else -> filtered
                    }
                }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pokemonTypes = listOf(
        "All types", "Normal", "Fire", "Water", "Electric", "Grass", "Ice",
        "Fighting", "Poison", "Ground", "Flying", "Psychic", "Bug",
        "Rock", "Ghost", "Dragon", "Dark", "Steel", "Fairy"
    )

    private val _pokemonDetail = MutableStateFlow<PokemonDetailResponse?>(null)
    val pokemonDetail: StateFlow<PokemonDetailResponse?> = _pokemonDetail

    fun loadPokemonDetail(id: String) {
        viewModelScope.launch {
            try {
                _pokemonDetail.value = repository.getPokemonById(id.toInt())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



}

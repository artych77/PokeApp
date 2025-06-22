package com.example.PokeApp.presentation.pokemon_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.PokeApp.data.local.PokemonEntity
import com.example.PokeApp.data.network.*
import com.example.PokeApp.data.repository.PokemonRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PokemonViewModel(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _pokemonDetail = MutableStateFlow<PokemonDetailResponse?>(null)
    val pokemonDetail: StateFlow<PokemonDetailResponse?> = _pokemonDetail

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description

    private val _evolutions = MutableStateFlow("")
    val evolutions: StateFlow<String> = _evolutions

    private val _evolutionChain = MutableStateFlow<EvolutionChainResponse?>(null)
    val evolutionChain: StateFlow<EvolutionChainResponse?> = _evolutionChain

    private val _selectedSort = MutableStateFlow("Smallest number")
    val selectedSort: StateFlow<String> = _selectedSort

    val sortOptions = listOf("Smallest number", "Largest number", "A–Z", "Z–A")

    private val _selectedType = MutableStateFlow("All types")
    val selectedType: StateFlow<String> = _selectedType

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    val pokemonTypes = listOf(
        "All types", "Normal", "Fire", "Water", "Electric", "Grass", "Ice",
        "Fighting", "Poison", "Ground", "Flying", "Psychic", "Bug",
        "Rock", "Ghost", "Dragon", "Dark", "Steel", "Fairy"
    )

    private val _favoriteSearchQuery = MutableStateFlow("")
    val favoriteSearchQuery: StateFlow<String> = _favoriteSearchQuery

    fun onSelectedSortChange(option: String) {
        _selectedSort.value = option
    }

    fun onSelectedTypeChange(type: String) {
        _selectedType.value = type
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onFavoriteSearchQueryChange(query: String) {
        _favoriteSearchQuery.value = query
    }

    val pokemonList: StateFlow<List<PokemonEntity>> =
        repository.getAllPokemon
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredPokemonList: StateFlow<List<PokemonEntity>> =
        combine(pokemonList, searchQuery, selectedType, selectedSort) { list, query, type, sort ->
            list.filter { pokemon ->
                val matchesQuery = pokemon.name.contains(query.trim(), ignoreCase = true)
                val matchesType = type == "All types" || pokemon.types.contains(type, ignoreCase = true)
                matchesQuery && matchesType
            }.let { filtered ->
                when (sort) {
                    "A–Z" -> filtered.sortedBy { it.name }
                    "Z–A" -> filtered.sortedByDescending { it.name }
                    "Smallest number" -> filtered.sortedBy { it.id }
                    "Largest number" -> filtered.sortedByDescending { it.id }
                    else -> filtered
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredFavorites: StateFlow<List<PokemonEntity>> =
        combine(pokemonList, searchQuery, selectedType, selectedSort) { list, query, type, sort ->
            list.filter { it.isFavorite }.filter { pokemon ->
                val matchesQuery = pokemon.name.contains(query.trim(), ignoreCase = true)
                val matchesType = type == "All types" || pokemon.types.contains(type, ignoreCase = true)
                matchesQuery && matchesType
            }.let { filtered ->
                when (sort) {
                    "A–Z" -> filtered.sortedBy { it.name }
                    "Z–A" -> filtered.sortedByDescending { it.name }
                    "Smallest number" -> filtered.sortedBy { it.id }
                    "Largest number" -> filtered.sortedByDescending { it.id }
                    else -> filtered
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleFavorite(pokemon: PokemonEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(pokemon)
        }
    }

    fun loadPokemonList() {
        viewModelScope.launch {
            repository.fetchPokemonListIfNeeded()
        }
    }

    fun loadPokemonDetail(id: String) {
        viewModelScope.launch {
            try {
                val detail = repository.getPokemonById(id.toInt())
                _pokemonDetail.value = detail

                val species = repository.getPokemonSpecies(id.toInt())

                // Description
                val desc = species.flavor_text_entries.firstOrNull { it.language.name == "en" }
                    ?.flavor_text?.replace("\n", " ")?.replace("\u000c", " ") ?: ""
                _description.value = desc

                // Evolution Chain
                val chainId = species.evolution_chain.url.split("/").filter { it.isNotBlank() }.last().toInt()
                val evolution = repository.getEvolutionChain(chainId)
                _evolutionChain.value = evolution

                // Extract names from chain
                val names = extractEvolutions(evolution.chain)
                _evolutions.value = names.joinToString(" → ") { it.replaceFirstChar { c -> c.uppercase() } }

            } catch (e: Exception) {
                e.printStackTrace()
                _description.value = "-"
                _evolutions.value = "-"
            }
        }
    }

    private fun extractEvolutions(chain: ChainLink): List<String> {
        val result = mutableListOf<String>()
        var current: ChainLink? = chain
        while (current != null) {
            result.add(current.species.name)
            current = current.evolves_to.firstOrNull()
        }
        return result
    }
}

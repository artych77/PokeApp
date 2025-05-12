package com.example.PokeApp.presentation.regions_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.PokeApp.data.network.RegionResult
import com.example.PokeApp.data.repository.PokemonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegionsViewModel(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _regions = MutableStateFlow<List<RegionResult>>(emptyList())
    val regions: StateFlow<List<RegionResult>> = _regions

    init {
        loadRegions()
    }

    private fun loadRegions() {
        viewModelScope.launch {
            _regions.value = repository.getRegions()
        }
    }
}

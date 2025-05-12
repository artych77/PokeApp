package com.example.PokeApp.presentation.regions_list

import androidx.lifecycle.ViewModel
import com.example.PokeApp.data.local.PokemonEntity
import com.example.PokeApp.data.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow

class RegionDetailViewModel(
    private val repository: PokemonRepository
) : ViewModel() {

    fun getPokemonByRegion(regionName: String): Flow<List<PokemonEntity>> {
        return repository.getLocalPokemonByRegion(regionName)
    }
}

package com.example.PokeApp.data.repository

import com.example.PokeApp.data.local.PokemonDao
import com.example.PokeApp.data.local.PokemonEntity
import com.example.PokeApp.data.network.EvolutionChainResponse
import com.example.PokeApp.data.network.PokemonApi
import com.example.PokeApp.data.network.PokemonApiInstance
import com.example.PokeApp.data.network.PokemonDetailResponse
import com.example.PokeApp.data.network.PokemonSpeciesResponse
import com.example.PokeApp.data.network.RegionResult
import kotlinx.coroutines.flow.Flow

class PokemonRepository(
    private val api: PokemonApi,
    private val dao: PokemonDao
) {
    val getAllPokemon: Flow<List<PokemonEntity>> = dao.getAllPokemon()

    suspend fun fetchPokemonListIfNeeded() {
        val localCount = dao.getCount()
        if (localCount >= 50) {
            return
        }

        try {
            val response = api.getPokemonList()
            val regionName = "kanto"
            val pokemonEntities = response.results.map { result ->
                val detail = api.getPokemonDetail(result.name)

                PokemonEntity(
                    id = detail.id,
                    name = detail.name.replaceFirstChar { it.uppercase() },
                    imageUrl = detail.sprites.front_default ?: "",
                    types = detail.types.joinToString(",") { it.type.name },
                    height = detail.height.toFloat(),
                    weight = detail.weight.toFloat(),
                    ability = detail.abilities.firstOrNull()?.ability?.name ?: "",
                    category = "",
                    description = "",
                    evolutions = "",
                    weaknesses = "",
                    region = regionName
                )
            }

            dao.clearPokemon()
            dao.insertAll(pokemonEntities)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getRegions(): List<RegionResult> {
        return api.getRegions().results
    }
    fun getLocalPokemonByRegion(regionName: String): Flow<List<PokemonEntity>> {
        return dao.getByRegion(regionName)
    }

    fun getFavoritePokemon(): Flow<List<PokemonEntity>> = dao.getFavoritePokemon()

    suspend fun toggleFavorite(pokemon: PokemonEntity) {
        dao.updateFavorite(pokemon.id, !pokemon.isFavorite)
    }

    suspend fun getPokemonById(id: Int): PokemonDetailResponse {
        return api.getPokemonDetail(id.toString())
    }

    suspend fun getPokemonSpecies(id: Int): PokemonSpeciesResponse {
        return api.getPokemonSpecies(id)
    }


    suspend fun getEvolutionChain(id: Int): EvolutionChainResponse {
        return api.getEvolutionChain(id)
    }



}

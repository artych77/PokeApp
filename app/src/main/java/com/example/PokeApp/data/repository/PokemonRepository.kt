package com.example.PokeApp.data.repository

import com.example.PokeApp.data.local.PokemonDao
import com.example.PokeApp.data.local.PokemonEntity
import com.example.PokeApp.data.network.PokemonApi
import com.example.PokeApp.data.network.PokemonApiInstance
import com.example.PokeApp.data.network.PokemonDetailResponse
import com.example.PokeApp.data.network.RegionResult
import kotlinx.coroutines.flow.Flow

class PokemonRepository(
    private val api: PokemonApi,
    private val dao: PokemonDao
) {
    val getAllPokemon: Flow<List<PokemonEntity>> = dao.getAllPokemon()

    /*suspend fun fetchPokemonList() {
        try {
            val response = api.getPokemonList()
            val pokemonEntities = response.results.map { result ->
                val detail = api.getPokemonDetail(result.name)
                PokemonEntity(
                    id = detail.id,
                    name = detail.name.capitalize(),
                    imageUrl = detail.sprites.front_default,
                    types = detail.types.joinToString(",") { it.type.name }
                )
            }
            dao.clearPokemon()
            dao.insertAll(pokemonEntities)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }*/
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
                    category = "", // <--- możesz uzupełnić z osobnego endpointu (species)
                    description = "", // <--- możesz uzupełnić z osobnego endpointu (species)
                    evolutions = "", // <--- osobna logika - można na razie zostawić pusty
                    weaknesses = "", // <--- trzeba wyliczyć z typów lub pominąć
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


}

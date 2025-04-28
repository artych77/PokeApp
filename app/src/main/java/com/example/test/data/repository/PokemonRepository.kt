package com.example.PokeApp.data.repository

import com.example.PokeApp.data.local.PokemonDao
import com.example.PokeApp.data.local.PokemonEntity
import com.example.PokeApp.data.network.PokemonApi
import com.example.PokeApp.data.network.PokemonApiInstance
import kotlinx.coroutines.flow.Flow

class PokemonRepository(
    private val api: PokemonApi,
    private val dao: PokemonDao
) {
    val getAllPokemon: Flow<List<PokemonEntity>> = dao.getAllPokemon()

    suspend fun fetchPokemonList() {
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
    }
}

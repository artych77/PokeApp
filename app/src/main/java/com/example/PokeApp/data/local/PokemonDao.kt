package com.example.PokeApp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao {
    @Query("SELECT * FROM pokemon_table")
    fun getAllPokemon(): Flow<List<PokemonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pokemonList: List<PokemonEntity>)

    @Query("DELETE FROM pokemon_table")
    suspend fun clearPokemon()

    @Query("SELECT COUNT(*) FROM pokemon_table")
    suspend fun getCount(): Int

    @Query("SELECT * FROM pokemon_table WHERE region = :regionName")
    fun getByRegion(regionName: String): Flow<List<PokemonEntity>>


}


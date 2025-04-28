package com.example.PokeApp.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PokemonApiInstance {
    val api: PokemonApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PokemonApi::class.java)
    }
}

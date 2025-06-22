package com.example.PokeApp.data.network


data class PokemonSpeciesResponse(
    val flavor_text_entries: List<FlavorTextEntry>,
    val evolution_chain: EvolutionChainUrl,
    val genera: List<GenusEntry>
)

data class FlavorTextEntry(
    val flavor_text: String,
    val language: Language
)

data class Language(
    val name: String
)

data class EvolutionChainUrl(
    val url: String
)

data class GenusEntry(
    val genus: String,
    val language: NamedApiResource
)
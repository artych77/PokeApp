package com.example.PokeApp.data.network

data class PokemonDetailResponse(
    val id: Int,
    val name: String,
    val sprites: Sprites,
    val types: List<TypeSlot>,
    val height: Int,
    val weight: Int,
    val base_experience: Int,
    val abilities: List<AbilityWrapper>,
    val stats: List<StatSlot>
)

data class Sprites(
    val front_default: String
)

data class TypeSlot(
    val slot: Int,
    val type: TypeInfo
)

data class TypeInfo(
    val name: String
)

data class StatSlot(
    val base_stat: Int,
    val stat: Stat
)

data class Stat(
    val name: String
)

data class AbilityWrapper(
    val ability: NamedApiResource
)

data class NamedApiResource(
    val name: String
)
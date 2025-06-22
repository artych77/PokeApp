package com.example.PokeApp.data.network

data class EvolutionChainResponse(
    val id: Int,
    val chain: ChainLink
)

data class ChainLink(
    val species: NamedApiResource,
    val evolves_to: List<ChainLink>
)

package com.example.PokeApp.data.network

data class RegionListResponse(
    val results: List<RegionResult>
)

data class RegionResult(
    val name: String,
    val url: String
)

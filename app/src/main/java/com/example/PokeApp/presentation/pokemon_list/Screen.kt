package com.example.PokeApp.presentation.pokemon_list

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Pokedex : Screen("pokemon_list", "Pokédex", Icons.Default.List)
    object Regions : Screen("regions", "Regions", Icons.Default.LocationOn)
    object Favorites : Screen("favorites", "Favorites", Icons.Default.Favorite)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)

    object RegionDetail : Screen("region_detail/{regionName}", "Region Details")

    fun regionDetailRoute(regionName: String): String = "region_detail/$regionName"
}

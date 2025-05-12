package com.example.PokeApp.presentation.regions_list

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.TopAppBar
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.PokeApp.data.local.AppDatabase
import com.example.PokeApp.data.repository.PokemonRepository
import com.example.PokeApp.data.network.PokemonApiInstance
import androidx.compose.foundation.clickable
import androidx.compose.runtime.LaunchedEffect
import com.example.PokeApp.presentation.pokemon_list.PokemonViewModel
import com.example.PokeApp.presentation.pokemon_list.PokemonViewModelFactory
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import com.example.PokeApp.presentation.pokemon_list.Screen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import com.example.PokeApp.presentation.regions_list.RegionsViewModel
import com.example.PokeApp.presentation.regions_list.RegionsViewModelFactory
import com.example.PokeApp.data.local.PokemonEntity
import com.example.PokeApp.presentation.regions_list.RegionDetailViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegionDetailScreen(
    regionName: String?,
    viewModel: RegionDetailViewModel = viewModel()
) {
    val pokemon by viewModel.getPokemonByRegion(regionName ?: "")
        .collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(regionName ?: "Region") })
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(pokemon) { p: PokemonEntity ->
                Text(
                    text = p.name,
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

package com.example.PokeApp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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










class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "pokemon-db"
            ).build()

            val repository = PokemonRepository(
                PokemonApiInstance.api,
                db.pokemonDao()
            )

            val viewModelFactory = PokemonViewModelFactory(repository)
            val viewModel: PokemonViewModel = viewModel(factory = viewModelFactory)

            LaunchedEffect(Unit) {
                viewModel.loadPokemonList()
            }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                PokedexApp(viewModel)
            }
        }
    }
}


@Composable
fun PokedexApp(viewModel: PokemonViewModel) {
    val navController = rememberNavController()
    NavGraph(navController = navController, viewModel = viewModel)
}



@Composable
fun NavGraph(navController: NavHostController, viewModel: PokemonViewModel) {
    NavHost(
        navController = navController,
        startDestination = "pokemon_list"
    ) {
        composable("pokemon_list") {
            PokemonListScreen(navController = navController, viewModel = viewModel)
        }
        composable("pokemon_detail/{pokemonId}") { backStackEntry ->
            val pokemonId = backStackEntry.arguments?.getString("pokemonId")
            PokemonDetailScreen(pokemonId)
        }
    }
}





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailScreen(pokemonId: String?) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Szczegóły Pokémon") })
        }
    ) { padding ->
        Text("Szczegóły dla Pokémon ID: $pokemonId", modifier = Modifier.padding(padding))
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonListScreen(
    navController: NavHostController,
    viewModel: PokemonViewModel
) {
    val pokemonList by viewModel.pokemonList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        errorMessage != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = errorMessage ?: "Wystąpił nieznany błąd")
            }
        }
        else -> {
            LazyColumn {
                items(pokemonList) { pokemon ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                navController.navigate("pokemon_detail/${pokemon.id}")
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Column {
                                Text(
                                    text = pokemon.name,
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    text = pokemon.types,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

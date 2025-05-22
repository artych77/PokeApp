package com.example.PokeApp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import coil.compose.AsyncImage
import com.example.PokeApp.data.local.AppDatabase
import com.example.PokeApp.data.local.PokemonEntity
import com.example.PokeApp.data.network.PokemonApiInstance
import com.example.PokeApp.data.repository.PokemonRepository
import com.example.PokeApp.presentation.pokemon_list.PokemonViewModel
import com.example.PokeApp.presentation.pokemon_list.PokemonViewModelFactory
import com.example.PokeApp.presentation.pokemon_list.Screen
import com.example.PokeApp.presentation.regions_list.RegionsViewModel
import com.example.PokeApp.presentation.regions_list.RegionsViewModelFactory


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "pokemon-db"
            )
                .fallbackToDestructiveMigration()
                .build()

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
                PokedexApp(viewModel, repository)

            }
        }
    }
}


@Composable
fun PokedexApp(viewModel: PokemonViewModel, repository: PokemonRepository) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { padding ->
        NavGraph(
            navController = navController,
            viewModel = viewModel,
            repository = repository,
            modifier = Modifier.padding(padding)
        )
    }
}





@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: PokemonViewModel,
    repository: PokemonRepository,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "pokemon_list",
        modifier = modifier
    ) {
        composable("pokemon_list") {
            PokemonListScreen(navController = navController, viewModel = viewModel)
        }
        composable(Screen.Regions.route) {
            val regionsViewModel: RegionsViewModel = viewModel(factory = RegionsViewModelFactory(repository))
            RegionsScreen(viewModel = regionsViewModel)
        }

        composable("pokemon_detail/{pokemonId}") { backStackEntry ->
            val pokemonId = backStackEntry.arguments?.getString("pokemonId")
            PokemonDetailScreen(pokemonId)
        }
        composable("favorites") {
            FavoritesScreen(viewModel = viewModel, navController = navController)
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
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredPokemonList by viewModel.filteredPokemonList.collectAsState()
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
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    placeholder = { Text("Szukaj Pokémonów...") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null)
                    }
                )

                LazyColumn {
                    items(filteredPokemonList) { pokemon ->
                        PokemonCard(pokemon, navController, viewModel)
                    }

                }

            }
        }
    }
}

@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    return navBackStackEntry?.destination?.route
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items: List<Screen> = listOf(
        Screen.Pokedex,
        Screen.Regions,
        Screen.Favorites,
        Screen.Profile
    )
    NavigationBar {
        val currentRoute = currentRoute(navController)
        items.forEach { screen ->
            NavigationBarItem(
                icon = { screen.icon?.let { Icon(it, contentDescription = screen.title) } },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegionsScreen(viewModel: RegionsViewModel = viewModel()) {
    val regionList by viewModel.regions.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Regions") })
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(regionList) { region ->
                Text(
                    text = region.name.replaceFirstChar { it.uppercase() },
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun PokemonCard(
    pokemon: PokemonEntity,
    navController: NavHostController,
    viewModel: PokemonViewModel
)
 {
    val type = pokemon.types.split(",").firstOrNull()?.trim() ?: ""
    val backgroundColor = getTypeColor(type)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { navController.navigate("pokemon_detail/${pokemon.id}") },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {

            // Ikona serduszka
            Icon(
                imageVector = if (pokemon.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorite",
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(24.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        viewModel.toggleFavorite(pokemon)
                    }
            )


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Lewa część (ID, nazwa, typy)
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "N°${pokemon.id.toString().padStart(3, '0')}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                    Text(
                        text = pokemon.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Row {
                        pokemon.types.split(",").forEach {
                            TypeChip(type = it.trim())
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                    }
                }

                // Prawa część (obrazek z gradientem pod spodem)
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(Color.White.copy(alpha = 0.3f), Color.Transparent),
                                center = Offset.Zero,
                                radius = 120f
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = pokemon.imageUrl,
                        contentDescription = pokemon.name,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
        }
    }
}



@Composable
fun TypeChip(type: String) {
    Box(
        modifier = Modifier
            .padding(end = 4.dp, top = 4.dp)
            .background(getTypeColor(type), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = type, style = MaterialTheme.typography.labelSmall, color = Color.White)
    }
}

fun getTypeColor(type: String): Color {
    return when (type.lowercase()) {
        "grass" -> Color(0xFF78C850)
        "poison" -> Color(0xFFA040A0)
        "fire" -> Color(0xFFF08030)
        "water" -> Color(0xFF6890F0)
        "bug" -> Color(0xFFA8B820)
        "normal" -> Color(0xFFA8A878)
        "electric" -> Color(0xFFF8D030)
        else -> Color.LightGray
    }
}

@Composable
fun FavoritesScreen(
    viewModel: PokemonViewModel,
    navController: NavHostController
) {
    val favoritePokemon by viewModel.favoritePokemon.collectAsState()

    LazyColumn {
        items(favoritePokemon) { pokemon ->
            PokemonCard(
                pokemon = pokemon,
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}






package com.example.PokeApp.presentation.regions_list

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.PokeApp.data.repository.PokemonRepository
import androidx.compose.runtime.collectAsState
import com.example.PokeApp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegionsScreen(
    viewModel: RegionsViewModel,
    navController: NavHostController,
    repository: PokemonRepository
)

 {
    val regionList by viewModel.regions.collectAsState()

    val backgroundMap = mapOf(
        "kanto" to R.drawable.bg_kanto,
        "johto" to R.drawable.bg_johto,
        "hoenn" to R.drawable.bg_hoenn,
        "sinnoh" to R.drawable.bg_sinnoh,
        "unova" to R.drawable.bg_unova,
        "kalos" to R.drawable.bg_kalos,
        "alola" to R.drawable.bg_alola,
        "galar" to R.drawable.bg_galar
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Regions") })
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(regionList) { region ->
                val name = region.name.lowercase()
                val generation = getGenerationForRegion(name)
                val backgroundRes = backgroundMap[name] ?: R.drawable.bg_kanto

                RegionCard(
                    name = name,
                    generation = generation,
                    backgroundRes = backgroundRes,
                    onClick = {
                        navController.navigate("region_detail/${name}")
                    }
                )
            }
        }
    }
}

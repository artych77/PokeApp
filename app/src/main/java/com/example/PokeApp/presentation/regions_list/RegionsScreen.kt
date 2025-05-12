package com.example.PokeApp.presentation.regions_list


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegionsScreen() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Regions") })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Tu będą regiony Pokémonów 🌍", style = MaterialTheme.typography.titleLarge)
        }
    }
}

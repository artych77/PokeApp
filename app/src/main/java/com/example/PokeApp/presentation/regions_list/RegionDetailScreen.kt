package com.example.PokeApp.presentation.regions_list

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.PokeApp.R

@Composable
fun RegionDetailScreen(regionName: String, navController: NavHostController) {
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

    val descriptionMap = mapOf(
        "kanto" to "Kanto to pierwszy region w grach Pokémon. To tutaj zaczęła się przygoda z grami Red/Blue.",
        "johto" to "Johto sąsiaduje z Kanto i zostało wprowadzone w grach Gold/Silver. Znane z Lugii i Ho-oh.",
        "hoenn" to "Region Hoenn ma tropikalny klimat i był bohaterem gier Ruby/Sapphire.",
        "sinnoh" to "Sinnoh, znane z gier Diamond/Pearl, ma górzysty teren i legendy o czasie i przestrzeni.",
        "unova" to "Unova to nowoczesny region inspirowany Nowym Jorkiem. Zadebiutował w Black/White.",
        "kalos" to "Kalos to region inspirowany Francją, znany z pięknej grafiki i Mega Ewolucji.",
        "alola" to "Alola to wyspiarski region z gier Sun/Moon. Wprowadził formy regionalne.",
        "galar" to "Galar to region inspirowany Wielką Brytanią. Zadebiutował w Pokémon Sword/Shield."
    )

    val imageRes = backgroundMap[regionName.lowercase()] ?: R.drawable.bg_kanto
    val description = descriptionMap[regionName.lowercase()] ?: "Brak opisu dla tego regionu."

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = regionName,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = regionName.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

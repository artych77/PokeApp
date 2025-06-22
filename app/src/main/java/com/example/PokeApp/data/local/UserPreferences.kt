package com.example.PokeApp.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreference(private val context: Context) {
    companion object {
        val NAME_KEY = stringPreferencesKey("name")
        val BIO_KEY = stringPreferencesKey("bio")
    }

    val nameFlow: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[NAME_KEY] ?: "" }

    val bioFlow: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[BIO_KEY] ?: "" }

    suspend fun saveName(name: String) {
        context.dataStore.edit { it[NAME_KEY] = name }
    }

    suspend fun saveBio(bio: String) {
        context.dataStore.edit { it[BIO_KEY] = bio }
    }
}

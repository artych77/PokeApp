package com.example.PokeApp.presentation.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.PokeApp.data.local.UserPreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = UserPreference(application)

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _bio = MutableStateFlow("")
    val bio: StateFlow<String> = _bio

    init {
        viewModelScope.launch {
            _name.value = prefs.nameFlow.first()
            _bio.value = prefs.bioFlow.first()
        }
    }

    fun onNameChange(newName: String) {
        _name.value = newName
    }

    fun onBioChange(newBio: String) {
        _bio.value = newBio
    }

    fun saveProfile() {
        viewModelScope.launch {
            prefs.saveName(_name.value)
            prefs.saveBio(_bio.value)
        }
    }
}

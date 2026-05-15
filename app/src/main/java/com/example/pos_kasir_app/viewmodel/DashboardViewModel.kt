package com.example.pos_kasir_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pos_kasir_app.model.User
import com.example.pos_kasir_app.repository.DashboardRepository
import com.example.pos_kasir_app.repository.Motd
import com.example.pos_kasir_app.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    private val dashboardRepository = DashboardRepository()
    private val profileRepository = ProfileRepository()
    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Idle)
    val uiState: StateFlow<DashboardUiState> = _uiState

    private val _profileState = MutableStateFlow<User?>(null)
    val profileState: StateFlow<User?> = _profileState

    private val _greetingState = MutableStateFlow<Motd?>(null)
    val greetingState: StateFlow<Motd?> = _greetingState

    init {
        fetchProfile()
        refreshDashboard()
    }

    fun fetchProfile() {
        _uiState.value = DashboardUiState.Loading

        viewModelScope.launch {
            try {
                val user = profileRepository.getCurrentUserProfile()
                _profileState.value = user
                _uiState.value = DashboardUiState.Success
            } catch (e: Exception) {
                _uiState.value = DashboardUiState.Error(
                    e.message ?: "Gagal memuat profil"
                )
            }
        }
    }

    fun refreshDashboard() {
        _uiState.value = DashboardUiState.Loading

        viewModelScope.launch {
            try {
                loadGreeting()
                _uiState.value = DashboardUiState.Success
            } catch (e: Exception) {
                _uiState.value = DashboardUiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun loadGreeting() {
        val message = dashboardRepository.getTimeOfDayMessage()
        _greetingState.value = message
    }
}
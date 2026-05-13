package com.example.pos_kasir_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pos_kasir_app.repository.DashboardRepository
import com.example.pos_kasir_app.repository.Motd
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    private val dashboardRepository = DashboardRepository()
    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Idle)
    val uiState: StateFlow<DashboardUiState> = _uiState

    private val _greetingState = MutableStateFlow<Motd?>(null)
    val greetingState: StateFlow<Motd?> = _greetingState

    private val _roleState = MutableStateFlow("")
    val roleState: StateFlow<String> = _roleState

    init {
        refreshDashboard()
    }

    fun refreshDashboard() {
        // Loading
        _uiState.value = DashboardUiState.Loading

        viewModelScope.launch {
            try {
                loadGreeting()
                loadRole()
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

    suspend fun loadRole() {
        val role = dashboardRepository.getRole()
        _roleState.value = role
    }
}
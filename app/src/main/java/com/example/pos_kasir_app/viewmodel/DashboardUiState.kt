package com.example.pos_kasir_app.viewmodel
/**
 * Handle states of Dashboard
 *
 * @property AuthUiState.Idle Idle State
 * @property AuthUiState.Loading Loading State
 * @property AuthUiState.Success Success State
 * @property AuthUiState.Error Error State
 */
sealed class DashboardUiState {
    object Idle: DashboardUiState()
    object Loading: DashboardUiState()
    object Success: DashboardUiState()
    data class Error(val message: String): DashboardUiState()
}
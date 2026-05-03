package com.example.pos_kasir_app.viewmodel

/**
 * Handle states of Auth
 *
 * @property AuthUiState.Idle Idle State
 * @property AuthUiState.Loading Loading State
 * @property AuthUiState.Success Success State
 * @property AuthUiState.Error Error State
 */
sealed class AuthUiState {
    object Idle: AuthUiState()
    object Loading: AuthUiState()
    object Success: AuthUiState()
    data class Error(val message: String) : AuthUiState()
}
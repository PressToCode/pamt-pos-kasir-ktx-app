package com.example.pos_kasir_app.viewmodel

/**
 * Handle states of Profile Screen
 *
 * @property ProfileUiState.Idle Idle State
 * @property ProfileUiState.Loading Loading State
 * @property ProfileUiState.Success Success State - profile loaded
 * @property ProfileUiState.Updating Updating profile
 * @property ProfileUiState.Updated Profile updated successfully
 * @property ProfileUiState.Error Error State
 */
sealed class ProfileUiState {
    object Idle : ProfileUiState()
    object Loading : ProfileUiState()
    object Success : ProfileUiState()
    object Updating : ProfileUiState()
    object Updated : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

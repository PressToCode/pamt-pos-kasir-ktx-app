package com.example.pos_kasir_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pos_kasir_app.model.User
import com.example.pos_kasir_app.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val repository = ProfileRepository()

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val uiState: StateFlow<ProfileUiState> = _uiState

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile

    // Editable fields
    private val _editNamaUser = MutableStateFlow("")
    val editNamaUser: StateFlow<String> = _editNamaUser

    private val _editTelpUser = MutableStateFlow("")
    val editTelpUser: StateFlow<String> = _editTelpUser

    init {
        fetchProfile()
    }

    fun fetchProfile() {
        _uiState.value = ProfileUiState.Loading

        viewModelScope.launch {
            try {
                val user = repository.getCurrentUserProfile()
                _userProfile.value = user
                _editNamaUser.value = user.namaUser
                _editTelpUser.value = user.telpUser ?: ""
                _uiState.value = ProfileUiState.Success
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(
                    e.message ?: "Gagal memuat profil"
                )
            }
        }
    }

    fun onNamaUserChange(value: String) {
        _editNamaUser.value = value
    }

    fun onTelpUserChange(value: String) {
        _editTelpUser.value = value
    }

    fun updateProfile() {
        _uiState.value = ProfileUiState.Updating

        viewModelScope.launch {
            try {
                repository.updateProfile(
                    namaUser = _editNamaUser.value,
                    telpUser = _editTelpUser.value
                )
                // Refresh the profile data
                val updatedUser = repository.getCurrentUserProfile()
                _userProfile.value = updatedUser
                _uiState.value = ProfileUiState.Updated
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(
                    e.message ?: "Gagal mengupdate profil"
                )
            }
        }
    }

    /**
     * Reset state back to Success after showing update confirmation
     */
    fun resetToSuccess() {
        _uiState.value = ProfileUiState.Success
    }
}

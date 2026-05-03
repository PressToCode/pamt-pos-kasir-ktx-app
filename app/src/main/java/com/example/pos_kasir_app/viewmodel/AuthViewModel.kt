package com.example.pos_kasir_app.viewmodel

import com.example.pos_kasir_app.repository.AuthRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import io.github.jan.supabase.auth.status.SessionStatus

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)

    private val _authCheckState = MutableStateFlow<AuthCheckState>(AuthCheckState.Checking)
    val authCheckState: StateFlow<AuthCheckState> = _authCheckState

    val uiState: StateFlow<AuthUiState> = _uiState

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _fullName = MutableStateFlow("")
    val fullName: StateFlow<String> = _fullName

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    init {
        observeAuthStatus()
    }

    private fun observeAuthStatus() {
        viewModelScope.launch {
            repository.sessionStatus.collect {
                status -> _authCheckState.value = when (status) {
                    is SessionStatus.Authenticated -> AuthCheckState.Authenticated
                    is SessionStatus.NotAuthenticated -> AuthCheckState.NotAuthenticated
                    is SessionStatus.Initializing -> AuthCheckState.Checking
                    is SessionStatus.RefreshFailure -> {
                        if (repository.isLoggedIn()) AuthCheckState.Authenticated
                        else AuthCheckState.NotAuthenticated
                    }
                }
            }
        }
    }

    fun onEmailChange(value: String) {
        _email.value = value
    }

    fun onFullNameChange(value: String) {
        _fullName.value = value
    }

    fun onPhoneChange(value: String) {
        _phone.value = value
    }

    fun onPasswordChange(value: String) {
        _password.value = value
    }

    fun login() {
        viewModelScope.launch {
            try {
                _uiState.value = AuthUiState.Loading

                repository.login(
                    email = _email.value,
                    password = _password.value
                )

                _uiState.value = AuthUiState.Success
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(
                    message = e.message ?: "Login gagal"
                )
            }
        }
    }

    fun register() {
        viewModelScope.launch {
            try {
                _uiState.value = AuthUiState.Loading

                repository.register(
                    fullName = _fullName.value,
                    email = _email.value,
                    phone = _phone.value,
                    password = _password.value
                )

                _uiState.value = AuthUiState.Success
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(
                    message = e.message ?: "Register gagal"
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()

            _uiState.value = AuthUiState.Idle
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}
package com.example.pos_kasir_app.viewmodel

import com.example.pos_kasir_app.repository.AuthRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.serialization.json.jsonPrimitive

data class UserProfile(
    val fullName: String,
    val email: String,
    val phone: String
)
class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    private val _authCheckState = MutableStateFlow<AuthCheckState>(AuthCheckState.Checking)
    val authCheckState: StateFlow<AuthCheckState> = _authCheckState

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _fullName = MutableStateFlow("")
    val fullName: StateFlow<String> = _fullName

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    // For User Profile
    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser

    init {
        observeAuthStatus()
    }

    private fun observeAuthStatus() {
        viewModelScope.launch {
            repository.sessionStatus.collect {
                status -> _authCheckState.value = when (status) {
                    is SessionStatus.Authenticated -> {
                        val user = status.session.user
                        val metadata = user?.userMetadata

                        _currentUser.value = UserProfile(
                            email = user?.email ?: "",
                            fullName = metadata?.get("full_name")?.jsonPrimitive?.content ?: "",
                            phone = metadata?.get("phone")?.jsonPrimitive?.content ?: ""
                        )

                        AuthCheckState.Authenticated
                    }
                    is SessionStatus.NotAuthenticated -> {
                        _currentUser.value = null
                        AuthCheckState.NotAuthenticated
                    }
                    is SessionStatus.Initializing -> AuthCheckState.Checking
                    is SessionStatus.RefreshFailure -> {
                        if (repository.isLoggedIn()) AuthCheckState.Authenticated
                        else {
                            _currentUser.value = null
                            AuthCheckState.NotAuthenticated
                        }
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
            _currentUser.value = null

            _uiState.value = AuthUiState.Idle
        }
    }

    fun resetState() {
        _email.value = ""
        _password.value = ""
        _fullName.value = ""
        _phone.value = ""
        _uiState.value = AuthUiState.Idle
    }
}
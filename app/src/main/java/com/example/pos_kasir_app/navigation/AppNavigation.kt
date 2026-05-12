package com.example.pos_kasir_app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.pos_kasir_app.ui.CashierScreen
import com.example.pos_kasir_app.ui.DashboardScreen
import com.example.pos_kasir_app.ui.LoginScreen
import com.example.pos_kasir_app.ui.RegisterScreen
import com.example.pos_kasir_app.viewmodel.AuthCheckState
import com.example.pos_kasir_app.viewmodel.AuthUiState
import com.example.pos_kasir_app.viewmodel.AuthViewModel
import com.example.pos_kasir_app.viewmodel.DashboardViewModel

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = viewModel(),
) {
    val authCheckState = authViewModel.authCheckState.collectAsStateWithLifecycle()

    /*
     * Saat aplikasi baru dibuka, cek dulu apakah user masih login.
     * Jangan langsung tampilkan LoginScreen.
     */
    when (authCheckState.value) {
        is AuthCheckState.Checking -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is AuthCheckState.Authenticated -> {
            MainNavigation(
                authViewModel = authViewModel,
                startDestination = Screen.NewDashboard
            )
        }

        is AuthCheckState.NotAuthenticated -> {
            MainNavigation(
                authViewModel = authViewModel,
                startDestination = Screen.Login
            )
        }
    }
}

@Composable
fun MainNavigation(
    authViewModel: AuthViewModel,
    startDestination: Screen
) {
    val navigationState = rememberNavigationState(
        startRoute = startDestination,
        topLevelRoutes = setOf(Screen.Login, Screen.NewDashboard)
    )

    val navigator = remember { Navigator(navigationState) }

    val fullName = authViewModel.fullName.collectAsStateWithLifecycle()
    val email = authViewModel.email.collectAsStateWithLifecycle()
    val phone = authViewModel.phone.collectAsStateWithLifecycle()
    val password = authViewModel.password.collectAsStateWithLifecycle()
    val uiState = authViewModel.uiState.collectAsStateWithLifecycle()
    val currentUser = authViewModel.currentUser.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.value) {
        if (uiState.value is AuthUiState.Success) {
            navigator.navigate(Screen.NewDashboard)
            authViewModel.resetState()
        }
    }

    val entryProvider = entryProvider<NavKey> {
        entry<Screen.Login> { _ ->
            LoginScreen(
                email = email.value,
                password = password.value,
                uiState = uiState.value,
                onEmailChange = authViewModel::onEmailChange,
                onPasswordChange = authViewModel::onPasswordChange,
                onLoginClick = {
                    authViewModel.login()
                },
                onNavigateToRegister = {
                    navigator.navigate(Screen.Register)
                }
            )
        }

        entry<Screen.Register> { _ ->
            RegisterScreen(
                fullName = fullName.value,
                email = email.value,
                phone = phone.value,
                password = password.value,
                uiState = uiState.value,
                onFullNameChange = authViewModel::onFullNameChange,
                onEmailChange = authViewModel::onEmailChange,
                onPhoneChange = authViewModel::onPhoneChange,
                onPasswordChange = authViewModel::onPasswordChange,
                onRegisterClick = {
                    authViewModel.register()
                },
                onNavigateToLogin = {
                    navigator.goBack()
                }
            )
        }

        entry<Screen.Dashboard> { _ ->
            DashboardScreen(
                onLogoutClick = {
                    authViewModel.logout()
                    navigator.navigate(Screen.Login)
                }
            )
        }

        entry<Screen.NewDashboard> { _ ->
            currentUser.value?.let { profile ->
                val dashboardViewModel: DashboardViewModel  = viewModel()

                CashierScreen(
                    userProfile = profile,
                    greetingMessage = dashboardViewModel.greetingState.value,
                    onLogoutClick = {
                        authViewModel.logout()
                        navigator.navigate(Screen.Login)
                    }
                )
            } ?: run {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    NavDisplay(
        entries = navigationState.toEntries(entryProvider),
        onBack = { navigator.goBack() }
    )
}

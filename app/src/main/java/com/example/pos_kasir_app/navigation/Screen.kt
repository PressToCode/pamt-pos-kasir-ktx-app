package com.example.pos_kasir_app.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen : NavKey {
    @Serializable
    data object Login : Screen()
    @Serializable
    data object Register : Screen()
    @Serializable
    data object Dashboard : Screen()

    @Serializable
    data object NewDashboard : Screen()
}

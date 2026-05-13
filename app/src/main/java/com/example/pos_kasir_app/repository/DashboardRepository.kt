package com.example.pos_kasir_app.repository

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Nightlight
import androidx.compose.material.icons.outlined.WbCloudy
import androidx.compose.material.icons.outlined.WbShade
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.pos_kasir_app.data.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

data class Motd(
    val greetingMessage: String,
    val icon: ImageVector
)

class DashboardRepository {
    private val supabase = SupabaseClientProvider.supabaseClient
    fun getTimeOfDayMessage(): Motd {
        val now = Clock.System.now()
        val hour = now.toLocalDateTime(TimeZone.currentSystemDefault()).hour

        return when (hour) {
            in 0..11 -> Motd(greetingMessage = "Good Morning!", icon = Icons.Outlined.WbSunny)
            in 12..16 -> Motd(greetingMessage = "Good Afternoon!", icon = Icons.Outlined.WbCloudy)
            in 17..20 -> Motd(greetingMessage = "Good Evening!", icon = Icons.Outlined.WbShade)
            else -> Motd(greetingMessage = "Good Night!", icon = Icons.Outlined.Nightlight)
        }
    }

    suspend fun getRole(): String {
        return supabase.postgrest.from("user")
            .select { filter { eq("id", supabase.auth.currentUserOrNull()?.id ?: "") } }
            .decodeSingle<Map<String, String>>()["role"] ?: "User"
    }
}
package com.example.pos_kasir_app.repository

import com.example.pos_kasir_app.data.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class DashboardRepository {
    private val supabase = SupabaseClientProvider.supabaseClient
    fun getTimeOfDayMessage(): String {
        val now = Clock.System.now()
        val hour = now.toLocalDateTime(TimeZone.currentSystemDefault()).hour

        return when (hour) {
            in 0..11 -> "Good Morning!"
            in 12..16 -> "Good Afternoon!"
            in 17..20 -> "Good Evening!"
            else -> "Good Night!"
        }
    }

    suspend fun getRole(): String? {
        try {
            return supabase.postgrest.from("user")
                .select { filter { eq("id", supabase.auth.currentUserOrNull()?.id ?: "") } }
                .decodeSingle<Map<String, String>>()["role"] ?: "User"
        } catch (e: Exception) {
            print(e)
            return null
        }
    }
}
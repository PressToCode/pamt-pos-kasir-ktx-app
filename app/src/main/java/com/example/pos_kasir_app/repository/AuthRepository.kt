package com.example.pos_kasir_app.repository

import com.example.pos_kasir_app.data.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.coroutines.flow.Flow

/**
 * Authentication Controller
 * W/ Supabase
 */
class AuthRepository {
    private val supabaseClient = SupabaseClientProvider.supabaseClient

    val sessionStatus: Flow<SessionStatus> = supabaseClient.auth.sessionStatus

    /**
     * Wrapper for registration
     * using suspend for async coroutines
     *
     * @param email user's email
     * @param password user's password
     */
    suspend fun register(email: String, password: String, fullName: String, phone: String) {
        supabaseClient.auth.signUpWith(Email) {
            this.email = email
            this.password = password
            this.data = buildJsonObject {
                put("full_name", fullName)
                put("phone", phone)
            }
        }
    }

    /**
     * Wrapper for Login
     * using suspend for async coroutines
     *
     * @param email user's email
     * @param password user's password
     */
    suspend fun login(email: String, password: String) {
        supabaseClient.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    /**
     * Wrapper for logout
     * using suspend for async coroutines
     */
    suspend fun logout() {
        supabaseClient.auth.signOut()
    }

    /**
     * Check if user is logged in
     * with try-catch and suspend
     *
     * @return Boolean
     */
    suspend fun isLoggedIn(): Boolean {
        try {
            supabaseClient.auth.awaitInitialization()
        } catch (e: Exception) {

        }

        return supabaseClient.auth.currentSessionOrNull() != null
    }

    /**
     * Wrapper for awaiting
     * auth init with suspend
     * for async coroutines
     */
    suspend fun awaitAuthInitialization() {
        supabaseClient.auth.awaitInitialization()
    }
}
package com.example.pos_kasir_app.repository

import com.example.pos_kasir_app.data.SupabaseClientProvider
import com.example.pos_kasir_app.model.User
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest

/**
 * Repository for Profile operations
 * Handles fetching and updating user profile data from Supabase 'user' table
 */
class ProfileRepository {
    private val supabase = SupabaseClientProvider.supabaseClient

    /**
     * Get the current user's profile from the 'user' table.
     *
     * @return User profile data from database
     * @throws Exception when user is not logged in or profile not found
     */
    suspend fun getCurrentUserProfile(): User {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: throw Exception("User belum login")

        return supabase.postgrest.from("user")
            .select { filter { eq("user_id", userId) } }
            .decodeSingle<User>()
    }

    /**
     * Update only nama_user and telp_user in the 'user' table.
     * Does NOT touch role, is_active, or other fields.
     *
     * @param namaUser new display name
     * @param telpUser new phone number
     */
    suspend fun updateProfile(namaUser: String, telpUser: String) {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: throw Exception("User belum login")

        supabase.postgrest.from("user")
            .update({
                set("nama_user", namaUser)
                set("telp_user", telpUser)
            }) {
                filter { eq("user_id", userId) }
            }
    }
}

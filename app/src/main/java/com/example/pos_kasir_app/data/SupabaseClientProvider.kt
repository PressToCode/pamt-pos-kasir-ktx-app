package com.example.pos_kasir_app.data

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClientProvider {
    val supabaseClient = createSupabaseClient(
        supabaseUrl = "https://ixtncazcwdlnbarmaluz.supabase.co",
        supabaseKey = "sb_publishable_59sWsrgje_90zaB08YYDKw_lp-1APBK"
    ) {
        install(Postgrest)
        install(Auth)
    }
}
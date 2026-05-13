package com.example.pos_kasir_app.repository

import com.example.pos_kasir_app.model.Kas
import com.example.pos_kasir_app.data.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class KasRepository {
    private val supabase = SupabaseClientProvider.supabaseClient

    suspend fun getAllKas(): List<Kas> {
        return withContext(Dispatchers.IO) {
            supabase.postgrest["kas"]
                .select()
                .decodeList<Kas>()
        }
    }

    suspend fun insertKas(kas: Kas) {
        withContext(Dispatchers.IO) {
            // Remove ID and created_at if it's an insert to let DB generate it, or simply pass the object if nullable fields are omitted
            supabase.postgrest["kas"]
                .insert(kas)
        }
    }

    suspend fun updateKas(kasId: String, kas: Kas) {
        withContext(Dispatchers.IO) {
            // Copy object to remove readonly fields or just update specific fields
            supabase.postgrest["kas"]
                .update(
                    {
                        set("nama_kas", kas.namaKas)
                        set("saldo_kas", kas.saldoKas)
                        set("keterangan_kas", kas.keteranganKas)
                        set("is_active", kas.isActive)
                    }
                ) {
                    filter { eq("kas_id", kasId) }
                }
        }
    }

    suspend fun deleteKas(kasId: String) {
        withContext(Dispatchers.IO) {
            supabase.postgrest["kas"]
                .delete {
                    filter { eq("kas_id", kasId) }
                }
        }
    }
}

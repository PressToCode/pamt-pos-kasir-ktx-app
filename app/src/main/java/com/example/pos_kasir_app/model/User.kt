package com.example.pos_kasir_app.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("user_id")
    val userId: String,

    @SerialName("nama_user")
    val namaUser: String,

    @SerialName("telp_user")
    val telpUser: String? = null,

    @SerialName("is_active")
    val isActive: Boolean = true,

    @SerialName("role")
    val role: String = "kasir",

    @SerialName("created_at")
    val createdAt: String? = null
)

package com.example.pos_kasir_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pos_kasir_app.model.Kas
import com.example.pos_kasir_app.repository.KasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class KasUiState {
    object Loading : KasUiState()
    data class Success(val kasList: List<Kas>) : KasUiState()
    data class Error(val message: String) : KasUiState()
}

class KasViewModel : ViewModel() {
    private val repository = KasRepository()

    private val _uiState = MutableStateFlow<KasUiState>(KasUiState.Loading)
    val uiState: StateFlow<KasUiState> = _uiState.asStateFlow()

    init {
        fetchKas()
    }

    fun fetchKas() {
        viewModelScope.launch {
            _uiState.value = KasUiState.Loading
            try {
                val list = repository.getAllKas()
                _uiState.value = KasUiState.Success(list)
            } catch (e: Exception) {
                _uiState.value = KasUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun addKas(namaKas: String, saldoKas: Double, keteranganKas: String, isActive: Boolean) {
        viewModelScope.launch {
            try {
                val newKas = Kas(
                    namaKas = namaKas,
                    saldoKas = saldoKas,
                    keteranganKas = keteranganKas.takeIf { it.isNotBlank() },
                    isActive = isActive
                )
                repository.insertKas(newKas)
                fetchKas() // Refresh list
            } catch (e: Exception) {
                _uiState.value = KasUiState.Error(e.message ?: "Failed to add Kas")
            }
        }
    }

    fun updateKas(kasId: String, namaKas: String, saldoKas: Double, keteranganKas: String, isActive: Boolean) {
        viewModelScope.launch {
            try {
                val updatedKas = Kas(
                    kasId = kasId,
                    namaKas = namaKas,
                    saldoKas = saldoKas,
                    keteranganKas = keteranganKas.takeIf { it.isNotBlank() },
                    isActive = isActive
                )
                repository.updateKas(kasId, updatedKas)
                fetchKas() // Refresh list
            } catch (e: Exception) {
                _uiState.value = KasUiState.Error(e.message ?: "Failed to update Kas")
            }
        }
    }

    fun deleteKas(kasId: String) {
        viewModelScope.launch {
            try {
                repository.deleteKas(kasId)
                fetchKas() // Refresh list
            } catch (e: Exception) {
                _uiState.value = KasUiState.Error(e.message ?: "Failed to delete Kas")
            }
        }
    }
}

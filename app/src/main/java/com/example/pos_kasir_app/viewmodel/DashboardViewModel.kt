package com.example.pos_kasir_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pos_kasir_app.repository.DashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    private val dashboardRepository = DashboardRepository()

    private val _greetingState = MutableStateFlow("")
    val greetingState: StateFlow<String> = _greetingState

    private val _roleState = MutableStateFlow("")
    val roleState: StateFlow<String> = _roleState

    init {
        loadGreetingMessage()
        loadRole()
    }

    fun loadGreetingMessage() {
        val message = dashboardRepository.getTimeOfDayMessage()
        _greetingState.value = message
    }

    fun loadRole() {
        viewModelScope.launch {
            val role = dashboardRepository.getRole()
            _roleState.value = role
        }
    }
}
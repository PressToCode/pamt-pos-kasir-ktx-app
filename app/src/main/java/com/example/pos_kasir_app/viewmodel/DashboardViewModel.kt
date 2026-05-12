package com.example.pos_kasir_app.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pos_kasir_app.repository.UtilRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DashboardViewModel : ViewModel() {
    private val utilRepository = UtilRepository();

    private val _greetingState = MutableStateFlow("")
    val greetingState: StateFlow<String> = _greetingState

    init {
        loadGreetingMessage()
    }

    fun loadGreetingMessage() {
        val message = utilRepository.getTimeOfDayMessage()
        _greetingState.value = message
    }
}
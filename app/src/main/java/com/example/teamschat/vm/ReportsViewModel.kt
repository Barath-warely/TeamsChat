package com.example.teamschat.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teamschat.data.repo.Repository
import com.example.teamschat.data.model.Chat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ------------------ UI State ------------------
data class ReportsUiState(
    val from: String = "",
    val to: String = "",
    val loading: Boolean = false,
    val data: List<Chat> = emptyList(),
    val error: String? = null
)

// ------------------ ViewModel ------------------
class ReportsViewModel(private val repo: Repository) : ViewModel() {

    private val _ui = MutableStateFlow(ReportsUiState())
    val ui: StateFlow<ReportsUiState> = _ui

    fun setFrom(date: String) {
        _ui.value = _ui.value.copy(from = date)
    }

    fun setTo(date: String) {
        _ui.value = _ui.value.copy(to = date)
    }

    fun fetch() {
        val f = _ui.value.from
        val t = _ui.value.to

        if (f.isBlank() || t.isBlank()) {
            _ui.value = _ui.value.copy(error = "Please select From and To dates")
            return
        }

        viewModelScope.launch {
            _ui.value = _ui.value.copy(loading = true, error = null)

            try {
                // Retrofit call → ReportResponse
                val response = repo.getMonthlyReport(f, t)

                // Only take chats from response
                _ui.value = _ui.value.copy(
                    loading = false,
                    data = response.chats,
                    error = null
                )
            } catch (e: Exception) {
                _ui.value = _ui.value.copy(
                    loading = false,
                    error = e.message ?: "Failed to load report"
                )
            }
        }
    }
}

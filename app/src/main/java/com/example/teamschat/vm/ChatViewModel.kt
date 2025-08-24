package com.example.teamschat.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teamschat.data.model.Chat
import com.example.teamschat.data.repo.Repository
import com.example.teamschat.data.storage.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class ChatViewModel: ViewModel() {
    private val repo = Repository()


    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading


    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats


    fun load() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _chats.value = repo.loadChats().data
            } finally { _loading.value = false }
        }
    }

    fun send(message: String, onDone: (() -> Unit)? = null) {
        viewModelScope.launch {
            try {
                val username = TokenManager.user?.name ?: "Unknown"
                val sent = repo.sendChat(message, username)
                _chats.value = _chats.value + sent
            } catch (_: Exception) { }
            onDone?.let { it() }
        }
    }

}
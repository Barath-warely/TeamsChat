package com.example.teamschat.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teamschat.data.model.User
import com.example.teamschat.data.repo.Repository
import com.example.teamschat.data.storage.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Error(val msg: String) : AuthState()
    data class Success(val token: String, val user: User) : AuthState()
}



class AuthViewModel: ViewModel() {
    private val repo = Repository()
    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state


    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                val res = repo.login(email, password)
                TokenManager.token = res.token
                TokenManager.user = res.user
                _state.value = AuthState.Success(res.token, res.user)
            } catch (e: Exception) {
                _state.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }
    fun register(name: String, phone: String, email: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                val res = repo.register(name, phone, email, password)
                TokenManager.token = res.token
                TokenManager.user = res.user
                _state.value = AuthState.Success(res.token, res.user)
            } catch (e: Exception) {
                Log.e("API", "Register failed", e)
                _state.value = AuthState.Error(e.message ?: "Register failed")
            }
        }
    }
}
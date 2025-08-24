package com.example.teamschat.data.repo

import com.example.teamschat.data.api.ServiceBuilder
import com.example.teamschat.data.model.AuthResponse
import com.example.teamschat.data.model.Chat
import com.example.teamschat.data.model.PageChats
import com.example.teamschat.data.model.ReportResponse


class Repository {
    private val api = ServiceBuilder.build()


    suspend fun login(email: String, password: String): AuthResponse =
        api.login(mapOf("email" to email, "password" to password))


    suspend fun register(name: String, phone: String, email: String, password: String): AuthResponse =
        api.register(mapOf("name" to name, "phone" to phone, "email" to email, "password" to password))


    suspend fun loadChats(): PageChats = api.getChats()
    suspend fun getMonthlyReport(from: String, to: String): ReportResponse {
        return api.getMonthlyReport(from, to)
    }

    suspend fun sendChat(message: String, name: String? = null): Chat =
        api.postChat(mapOf("message" to message, "name" to (name ?: "General")))
}
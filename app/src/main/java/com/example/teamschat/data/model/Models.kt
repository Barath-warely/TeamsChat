package com.example.teamschat.data.model

data class User(
    val id: Int,
    val name: String,
    val phone: String?,
    val email: String
)


data class AuthResponse(
    val user: User,
    val token: String
)


data class Chat(
    val id: Int,
    val user_id: Int,
    val name: String?,
    val message: String,
    val created_at: String,
    val updated_at: String?,
    val user: User?
)


data class PageChats(
    val current_page: Int,
    val data: List<Chat>
)
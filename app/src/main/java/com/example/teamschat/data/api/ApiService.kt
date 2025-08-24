package com.example.teamschat.data.api

import com.example.teamschat.data.model.AuthResponse
import com.example.teamschat.data.model.Chat
import com.example.teamschat.data.model.PageChats
import retrofit2.http.*


interface ApiService {

    @POST("api/register")
    suspend fun register(@Body body: Map<String, String>): AuthResponse

    @POST("api/login")
    suspend fun login(@Body body: Map<String, String>): AuthResponse

    @GET("api/chats")
    suspend fun getChats(@Query("page") page: Int? = null): PageChats

    @POST("api/chats")
    suspend fun postChat(@Body body: Map<String, String>): Chat
}



object ApiClient {
    // Use 10.0.2.2 for Emulator to talk to localhost
    const val BASE_URL = "http://192.168.43.123:8000/"
}
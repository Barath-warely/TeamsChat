package com.example.teamschat.data.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.teamschat.data.model.User
import com.google.gson.Gson


object TokenManager {
    private const val SP_NAME = "teams_prefs"
    private const val KEY_TOKEN = "token"
    private const val KEY_USER = "user"
    private lateinit var sp: SharedPreferences
    private val gson = Gson()


    fun init(ctx: Context) { sp = ctx.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE) }


    var token: String?
        get() = sp.getString(KEY_TOKEN, null)
        set(value) { sp.edit { putString(KEY_TOKEN, value) } }


    var user: User?
        get() = sp.getString(KEY_USER, null)?.let { gson.fromJson(it, User::class.java) }
        set(value) { sp.edit { putString(KEY_USER, gson.toJson(value)) } }


    fun clear() { sp.edit { remove(KEY_TOKEN); remove(KEY_USER) } }
}
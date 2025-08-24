package com.example.teamschat

import android.app.Application
import com.example.teamschat.data.storage.TokenManager


class App: Application() {
    override fun onCreate() {
        super.onCreate()
        TokenManager.init(this)
    }
}
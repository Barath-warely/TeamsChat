package com.example.teamschat.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.teamschat.data.storage.TokenManager
import com.example.teamschat.ui.auth.LoginActivity


class SplashActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (TokenManager.token.isNullOrBlank()) {
            startActivity(Intent(this, LoginActivity::class.java))
        }else {
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish()
    }
}
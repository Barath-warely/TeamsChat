package com.example.teamschat.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.teamschat.databinding.ActivityLoginBinding
import com.example.teamschat.ui.main.MainActivity
import com.example.teamschat.vm.AuthState
import com.example.teamschat.vm.AuthViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class LoginActivity: AppCompatActivity() {
    private lateinit var b: ActivityLoginBinding
    private val vm: AuthViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        b = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(b.root)


        b.btnLogin.setOnClickListener {
            vm.login(b.inputEmail.text.toString().trim(), b.inputPassword.text.toString())
        }


        b.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
        MainScope().launch {
            vm.state.collectLatest { st ->
                when(st){
                    is AuthState.Idle -> { b.progress.visibility = View.GONE }
                    is AuthState.Loading -> { b.progress.visibility = View.VISIBLE }
                    is AuthState.Success -> {
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java)); finish() }
                    is AuthState.Error -> { b.progress.visibility = View.GONE; Toast.makeText(this@LoginActivity, "Invalid credentials", Toast.LENGTH_LONG).show() }
                }
            }
        }
    }
}
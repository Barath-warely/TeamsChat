package com.example.teamschat.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.teamschat.databinding.ActivityRegisterBinding
import com.example.teamschat.ui.main.MainActivity
import com.example.teamschat.vm.AuthState
import com.example.teamschat.vm.AuthViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var b: ActivityRegisterBinding
    private val vm: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        b = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(b.root)

        // Register button
        b.btnRegister.setOnClickListener {
            val firstName = b.inputFirstName.text.toString().trim()
            val lastName = b.inputLastName.text.toString().trim()
            val phone = b.inputPhone.text.toString().trim()
            val email = b.inputEmail.text.toString().trim()
            val password = b.inputPassword.text.toString()

            if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fullName = "$firstName $lastName"
            vm.register(fullName, phone, email, password)
        }

        // Back to login
        b.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Observe state
        lifecycleScope.launch {
            vm.state.collectLatest { st ->
                when (st) {
                    is AuthState.Idle -> b.progress.visibility = View.GONE
                    is AuthState.Loading -> b.progress.visibility = View.VISIBLE
                    is AuthState.Success -> {
                        Toast.makeText(this@RegisterActivity, "Registered successfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()
                    }
                    is AuthState.Error -> {
                        b.progress.visibility = View.GONE
                        Toast.makeText(this@RegisterActivity, st.msg, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}

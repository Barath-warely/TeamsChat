package com.example.teamschat.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.teamschat.R
import com.example.teamschat.data.storage.TokenManager
import com.example.teamschat.databinding.ActivityMainBinding
import com.example.teamschat.ui.auth.LoginActivity
import com.example.teamschat.ui.chat.ChatFragment
import com.google.android.material.navigation.NavigationView


class MainActivity: AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var b: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)


        setSupportActionBar(b.toolbar)
        val toggle = ActionBarDrawerToggle(this, b.drawer, b.toolbar, R.string.nav_open, R.string.nav_close)
        b.drawer.addDrawerListener(toggle)
        toggle.syncState()
        b.navView.setNavigationItemSelectedListener(this)


// Load Chat fragment by default
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ChatFragment())
            .commit()
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_chats -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ChatFragment()).commit()
            R.id.menu_reports -> { /* TODO: start reports activity or fragment */ }
            R.id.menu_settings -> { /* TODO: start settings */ }
            R.id.menu_logout -> {
                TokenManager.clear()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
        b.drawer.closeDrawer(GravityCompat.START)
        return true
    }
}
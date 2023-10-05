package com.example.electriccarsapp.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.electriccarsapp.R
import com.example.electriccarsapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment)
        setupWithNavController(binding.bottomNavigation, navController)
        setupListeners()

    }

    private fun setupListeners(){
        binding.fabCalcular.setOnClickListener {
            startActivity(Intent(this, CalcularAutonomiaActivity::class.java))
        }
    }



}



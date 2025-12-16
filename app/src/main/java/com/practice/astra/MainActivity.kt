package com.practice.astra

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.practice.astra.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ボトムナビゲーション設定
        navController = findNavController(R.id.nav_host_fragment_activity_main)
        setupBottomNavigation(binding.navView)
    }

    /** 関数定義エリア */
    // ボトムナビゲーション設定関数定義
    private fun setupBottomNavigation(navView: BottomNavigationView) {
        navView.setOnItemSelectedListener { item ->
            val destinationId = item.itemId
            val currentId = navController.currentDestination?.id

            if (destinationId != currentId && navView.menu.findItem(destinationId) != null) {
                navController.navigate(destinationId)
                Log.d("test", "true")
                return@setOnItemSelectedListener true
            }
            Log.d("test", "false")
            return@setOnItemSelectedListener false
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            navView.menu.findItem(destination.id)?.isChecked = true
        }
    }
}
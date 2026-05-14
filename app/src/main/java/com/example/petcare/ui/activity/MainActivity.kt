package com.example.petcare.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.petcare.R
import com.example.petcare.databinding.ActivityMainBinding
import com.example.petcare.ui.fragment.DashboardFragment
import com.example.petcare.ui.fragment.PetListFragment
import com.example.petcare.ui.fragment.SettingsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set default fragment
        loadFragment(DashboardFragment())

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_pets -> loadFragment(PetListFragment())
                R.id.nav_appointments -> loadFragment(DashboardFragment()) // Placeholder
                R.id.nav_settings -> loadFragment(SettingsFragment())
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}

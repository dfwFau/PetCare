package com.example.petcare.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.petcare.database.AppDatabase
import com.example.petcare.databinding.ActivityRegisterBinding
import com.example.petcare.repository.UserRepository
import com.example.petcare.viewmodel.UserViewModel
import com.example.petcare.viewmodel.ViewModelFactory

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = UserRepository(AppDatabase.getDatabase(this).userDao())
        viewModel = ViewModelProvider(this, ViewModelFactory(repository))[UserViewModel::class.java]

        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.register(username, password)
            }
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }

        viewModel.registerResult.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

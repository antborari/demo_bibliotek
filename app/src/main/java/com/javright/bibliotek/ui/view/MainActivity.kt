package com.javright.bibliotek.ui.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.javright.bibliotek.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.goToQR.setOnClickListener {
            finish()
            startActivity(Intent(this, ScanActivity::class.java))
        }
        binding.goToBooks.setOnClickListener {
            startActivity(Intent(this, BookActivity::class.java))
        }
    }

}
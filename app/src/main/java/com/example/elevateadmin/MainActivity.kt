package com.example.elevateadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.elevateadmin.addClass.ShowClassActivity
import com.example.elevateadmin.addTrainer.ShowTrainerActivity
import com.example.elevateadmin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addClass.setOnClickListener {
            startActivity(Intent(this, ShowClassActivity::class.java))
        }

        binding.addTrainer.setOnClickListener {
            startActivity(Intent(this, ShowTrainerActivity::class.java))
        }
    }
}
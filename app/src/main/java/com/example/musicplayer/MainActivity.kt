package com.example.musicplayer

import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.musicplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        grantPermission()
    }

    private fun grantPermission(): Boolean {
        var result: Boolean = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            result =  when {
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    true
                }
                shouldShowRequestPermissionRationale(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) -> {
                    Toast.makeText(this, "Read Permission is required", Toast.LENGTH_LONG).show()
                    false
                }
                else -> {
                    registerForActivityResult(ActivityResultContracts.RequestPermission()) {}
                        .launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    false
                }
            }
        }
        return result
    }
}
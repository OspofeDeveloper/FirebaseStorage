package com.example.firebasestorage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.firebasestorage.databinding.ActivityMainBinding
import com.example.firebasestorage.databinding.ActivityUploadXmlBinding
import com.example.firebasestorage.ui.compose.upload.UploadComposeActivity
import com.example.firebasestorage.ui.xml.upload.UploadXmlActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        initListeners()
    }

    private fun initListeners() {

        binding.btnNavigateToXml.setOnClickListener {
            startActivity(UploadXmlActivity.create(this))
        }

        binding.btnNavigateToCompose.setOnClickListener {
            startActivity(UploadComposeActivity.create(this))
        }

    }
}
package com.example.firebasestorage.ui.xml.list

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.firebasestorage.databinding.ActivityListXmlBinding
import com.example.firebasestorage.ui.xml.list.adapter.GalleryAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListXmlActivity : AppCompatActivity() {

    companion object {
        fun create(context: Context) = Intent(context, ListXmlActivity::class.java)
    }

    private lateinit var binding: ActivityListXmlBinding
    private val listXmlViewModel: ListXmlViewModel by viewModels()
    private lateinit var galleryAdapter: GalleryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListXmlBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        listXmlViewModel.getAllImages()
    }

    private fun initUI() {
        initUIState()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        galleryAdapter = GalleryAdapter()

        binding.rvGallery.apply {
            layoutManager = GridLayoutManager(this@ListXmlActivity, 2)
            adapter = galleryAdapter
        }

    }

    private fun initUIState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                listXmlViewModel.uiState.collect {
                    galleryAdapter.updateList(it.images)
                    binding.pbGallery.isVisible = it.isLoading
                }
            }
        }
    }
}
package com.example.firebasestorage.ui.compose.list

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.firebasestorage.R
import com.example.firebasestorage.databinding.ActivityListComposeBinding
import com.example.firebasestorage.databinding.ActivityListXmlBinding
import com.example.firebasestorage.databinding.ActivityUploadComposeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListComposeActivity : AppCompatActivity() {

    companion object {
        fun create(context: Context) = Intent(context, ListComposeActivity::class.java)
    }

    lateinit var binding: ActivityListComposeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListComposeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.composeView.setContent {
            ListComposeScreen()
        }
    }

    @Composable
    fun ListComposeScreen() {
        val listComposeViewModel: ListComposeViewModel by viewModels()
        val uiState by listComposeViewModel.uiState.collectAsState()

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                items(uiState.images) {
                    Card(Modifier.fillMaxSize(), shape = RoundedCornerShape(24)) {
                        AsyncImage(
                            model = it,
                            contentDescription = "",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(50.dp),
                    color = colorResource(id = R.color.green),
                )
            }
        }
    }
}
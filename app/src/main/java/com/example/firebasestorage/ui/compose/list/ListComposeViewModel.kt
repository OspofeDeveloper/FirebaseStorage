package com.example.firebasestorage.ui.compose.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebasestorage.data.StorageService
import com.example.firebasestorage.ui.xml.list.ListUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ListComposeViewModel @Inject constructor(private val storageService: StorageService) :
    ViewModel() {

    private var _uiState = MutableStateFlow(ListUIState(false, emptyList()))
    val uiState: StateFlow<ListUIState> = _uiState


    init {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = withContext(Dispatchers.IO) {
                storageService.getAllImages().map { it.toString() }
            }

            _uiState.value = _uiState.value.copy(isLoading = false, images = result)
        }
    }
}

data class ListUIState(
    val isLoading: Boolean,
    val images: List<String>
)
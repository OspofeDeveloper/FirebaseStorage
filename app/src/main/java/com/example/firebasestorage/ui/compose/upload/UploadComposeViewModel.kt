package com.example.firebasestorage.ui.compose.upload

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebasestorage.data.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UploadComposeViewModel @Inject constructor(
    private val storageService: StorageService
): ViewModel(){

    private var _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun uploadBasicImage(uri: Uri) {
        storageService.uploadBasicImage(uri)
    }

    fun uploadAndGetImage(uri: Uri, onSuccessDownload: (Uri) -> (Unit)) {

        //Siempre que usemos los listeners y pueda devolver una excepción, metemos el código en
        //un bloque try/catch. Desde el catch podemos mandar una lambda a activity para que ejecute
        //el mensaje
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val result =
                    withContext(Dispatchers.IO) { storageService.uploadAndDownloadImage(uri) }
                onSuccessDownload(result)
            } catch (e: Exception) {
                Log.i("Error", e.message.orEmpty())
            }

            _isLoading.value = false
        }

    }
}
package com.example.firebasestorage.data

import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject

class StorageService @Inject constructor(
    private val firebaseStorage: FirebaseStorage
) {

}
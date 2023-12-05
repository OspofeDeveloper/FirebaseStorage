package com.example.firebasestorage.data

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject

class StorageService @Inject constructor(
    private val firebaseStorage: FirebaseStorage
) {

    /**
     * Primero necesitamos una referencia para saber donde vamos a almacenar la info, indicandole
     * el nombre del fichero que hemos creado en createFile() de UploadXmlActivity.
     * El uri.lastPathSegment saca ese nombre y indicamos orEmpty porque el child es de tipo
     * String?. Aunque sabemos que nunca lo va a ser ya que al lanzar el intent comprobamos que el
     * path de la uri no sea nulo.
     *
     * Con esto si vamos a FirebaseStorage veremos que esta imagen se nos guarda
     */
    fun uploadBasicImage(uri: Uri) {
        val reference = firebaseStorage.reference.child(uri.lastPathSegment.orEmpty())
        reference.putFile(uri)
    }

}
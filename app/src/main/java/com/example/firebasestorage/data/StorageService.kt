package com.example.firebasestorage.data

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.storageMetadata
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

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

    /**
     * Lo primero que tenemos que hacer para leer una imagen es buscar la referencia a la que queremos
     * acceder.
     *
     * Para saber de donde tengo que sacar las fotos parece que hay veces que no sabremos de que
     * path tenemos que sacar esa foto, pero una de las cosas que mas se suelen hacer es recuperar
     * el usuario que está loggeado a partir de inyectar en esta clase un firebaseAuth como el
     * que hicimos en el apartado de Login, recuperar el usuario a partir de este y con ese podemos
     * poner su userId dentro de child.
     *
     * Si después seguimos la misma estructura para guardar imágenes con todos los usuarios, simplemente
     * cambiando el id del usuario en cada caso ya tendriamos la foto que quisiesemos en cada momento.
     *
     * Un ejemplo de como podriamos usar un metodo de estos seria con un metodo que se llame por
     * ejemplo downloadProfileImage y cuando nosotros navefgamos hasta el perfil de nuestra
     * aplicación llamamos a este método en un principio para recuperar una uri, y con esa uri ya
     * solo tendriamos que cargar la imagen en algún componente.
     *
     * Entonces con esta funcion lo que hacemo es recuperar la Uri de esa imagen a descargar, pero para
     * cargar la uri que necesitamos, necesitaremos alguna dependencia (Glide en xml)
     */
    suspend fun downloadBasicImage(): Uri {
        //val reference = firebaseStorage.reference.child("$userId/profile.png")
        val reference =
            firebaseStorage.reference.child("ejemplo/International_Pokémon_logo.svg.png")

        /*
        Recuperamos la uri de nuestra reference
        El problema es que es una task que es un código que se está ejecutando pero que no sabemos
        cuando se va a resolver, por lo tanto esperamos a que acabe con await().

        Otra forma de hacerlo es en lugar del await() usar el suspendCancellableCoroutine para así
        aprovechar las funcionaldades de los listeners y tener más control
        */
        return reference.downloadUrl.await()
    }

    suspend fun uploadAndDownloadImage(uri: Uri): Uri {
        readMetadataAdvanced()
        return Uri.EMPTY
        /*return suspendCancellableCoroutine<Uri> { cancellableContinuation ->
            val reference = firebaseStorage.reference.child("download/${uri.lastPathSegment}")

            reference.putFile(uri, createMetadata())
                .addOnSuccessListener { referenceToWhereImageIs ->
                    downloadImage(referenceToWhereImageIs, cancellableContinuation)
                }.addOnFailureListener {
                cancellableContinuation.resumeWithException(it)
            }
        }*/
    }

    private fun downloadImage(
        uploadTask: UploadTask.TaskSnapshot,
        cancellableContinuation: CancellableContinuation<Uri>
    ) {
        uploadTask.storage.downloadUrl
            .addOnSuccessListener { uri -> cancellableContinuation.resume(uri) }
            .addOnFailureListener { cancellableContinuation.resumeWithException(it) }
    }

    /**
     * Los metadatos no son mas que ciertos datos que nos dicen cosas especificas de, en nuestro caso,
     * las imagenes. Estos metadatos son conjuntos clave-valor y podemos usar ciertos metadatos
     * que vienen definidos como contentType, al igual que ciertos metadatos customizados según
     * lo que nosotros queramos. Lo único es que setCustomMetadata solo coje strings, asi que si le
     * queremos meter cualquier otro tipo de valor lo tendremos que transformar a String.
     *
     * Estos metadatos los podemos usar cuando vamos a subir la imagen, en la función putFile podemos
     * añadirle esos metadatos a la imagen que le pasamos por uri.
     *
     * Y por ultimo, si metemos metadatos en un string que ya tiene, los antiguos son reemplazados
     * por los nuevos
     */
    private fun createMetadata(): StorageMetadata {
        val metadata = storageMetadata {
            contentType = "image/jpeg"
            setCustomMetadata("date", "25-04-2002")
            setCustomMetadata("anyKey", "anyValue")
        }
        return metadata
    }

    /**
     * Este método en lugar de crear metadatos como createMetadata, nos permite leer los metadatos
     * de una imagen.
     *
     * Para ello en este caso obviamente necesitamos la referencia de una imagen que exista y esta
     * imagen tiene que contener metadatos. En el caso de que no los tenga se nos devolvera un string
     * vacio
     */
    private suspend fun readMetadataBasic() {
        val reference =
            firebaseStorage.reference.child("download/metadata6547209743537309110.jpg")

        val response = reference.metadata.await()
        val metainfo = response.getCustomMetadata("date")
        Log.i("Ospofe Metainfo", metainfo.orEmpty())
    }

    /**
     * En este metodo miraremos como podemos recuperar todos los metadatos de la imagen.
     */
    private suspend fun readMetadataAdvanced() {
        val reference =
            firebaseStorage.reference.child("download/metadata6547209743537309110.jpg")

        val response = reference.metadata.await()
        response.customMetadataKeys.forEach { key ->
            response.getCustomMetadata(key)?.let {value ->
                Log.i("Ospofe Metadata", "Para la key: $key el valor es $value")
            }
        }
    }
}
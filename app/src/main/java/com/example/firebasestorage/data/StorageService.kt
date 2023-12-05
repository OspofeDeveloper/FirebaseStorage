package com.example.firebasestorage.data

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
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
     * cargar la uri que necesitamos, necesitaremos alguna dependencia
     */
    suspend fun downloadBasicImage(): Uri {
        //val reference = firebaseStorage.reference.child("$userId/profile.png")
        val reference = firebaseStorage.reference.child("ejemplo/International_Pokémon_logo.svg.png")

        //Recuperamos la uri de nuestra reference
        //El problema es que es una task que es un código que se está ejecutando pero que no sabemos
        //cuando se va a resolver, por lo tanto esperamos a que acabe con await()
        return reference.downloadUrl.await()
    }

}
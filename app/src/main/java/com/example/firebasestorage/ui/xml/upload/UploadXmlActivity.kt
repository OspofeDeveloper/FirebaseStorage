package com.example.firebasestorage.ui.xml.upload

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import com.example.firebasestorage.R
import com.example.firebasestorage.databinding.ActivityUploadXmlBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects

@AndroidEntryPoint
class UploadXmlActivity : AppCompatActivity() {

    companion object {
        fun create(context: Context): Intent = Intent(context, UploadXmlActivity::class.java)
    }

    private lateinit var binding: ActivityUploadXmlBinding
    private val uploadXmlViewModel: UploadXmlViewModel by viewModels()

    /**
     * Para guardar fotos tomadas con la cámara del mobil lo implementaremos con intentLauchers y de
     * una forma que no hace falta permisos de cámara.
     *
     * Para generar una Uri adecuada lanzaremos un Intent que va a abrir la cámara del móbil, este
     * intent va a sacar una foto, pero para sacar esa foto le vamos a mandar una Uri, y esa Uri la
     * habremos creado nosotros con un fichero temporal, es decir, vamos a crear un fichero temporal
     * para recuperar su Uri y decirle al intent que cuando saque esa foto la va a almacenar en esa
     * Uri y cuando recuperemos lo que necesitemos, ese fichero lo vamos a eliminar.
     *
     * Para hacer lo de la URI crearemos la misma cada vez que lancemos el intent, de tal forma que
     * siempre que vayamos a hacerlo creemos ese archivo temporal, y lo hacemos siempre con instancias
     * de URI diferentes porque si usaramos por ejemplo una constante estariamos subiendo siempre la
     * misma foto, ya que el fichero temporal no cambiaria. Por eso creamos la variable URI como
     * lateinit var y cada vez que queramos una nueva instancia de esta, llamaremos al método
     * private fun generateUri()
     *
     * Si nos fijamos lo que nos devuelve esta función es un Boolean, el cual solo nos indica si
     * ha salido bien o no, es decir, si consiguió o no configurar bien la Uri que nosotros le pasamos.
     *
     * Entonces el código que le indicamos dentro de las {} es aquel que se ejecuta cuando termine
     * el proceso de lanzar el intent. Entonces:
     *      - Si ha habido Success ->
     */
    private lateinit var uri: Uri

    private val intentCameraLauncher = registerForActivityResult(TakePicture()) {
        if (it && uri.path?.isNotEmpty() == true) { //Si da success y path de la uri no es nulo
            uploadXmlViewModel.uploadBasicImage(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadXmlBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        initListeners()
    }

    private fun initListeners() {
        binding.fabImage.setOnClickListener { takePhoto() }
    }

    /**
     * Recuperamos la uri y lanzamos la cámara con esa uri creada
     */
    private fun takePhoto() {
        generateUri()
        intentCameraLauncher.launch(uri)
    }

    /**
     * Hay diferentes formas de hacer esto, una para versiones mas antiguas de Android y otra para
     * versiones iguales o superior a Android 13. Veremos ambas:
     *
     * Para versiones de Android 13 o superior hay que crear un provider, que no es más que un
     * componente que nos permite compartir información con otras aplicaciones, ya que cuando nosotros
     * saquemos el intent de la cámara no estamos metiendo la camara realmente en nuestro móbil, sinoq
     * que estamos lanzando la aplicación de la cámara del usuario y luego estamos volviendo, entonces
     * necesitamos compartir esa información entre ambas aplicaciones.
     *
     * Ese provider lo vamos a crear en nuestro AndroidManifest.
     *
     * Entonces para crear la uri tendremos que pasarle 3 parametros:
     *      - Contexto -> Podemos pasar this, pero tenemos que estar tan seguros de que no sea null
     *                    que lo que haremos es comprobar que no es nulo con Objects.requireNonNull
     *      - Authority -> El campo authorities del Provider
     *      - File -> Es nuestro fichero temporal, por lo que no tenemos que tener ese fichero en
     *                ningún sitio persistido. El problema de esto es que si solo creamos uno si
     *                siempre le ponemos el mismo nombre vamos a estar reemplazando las imagenes
     *                subidas con el mismo file, lo que quere decir que en caso de haber 2 imagenes
     *                con el mismo file, la antiguo se borra al intentar meter la nueva. Entonces
     *                tenemos que ponerles siempre nombres distintos.
     *                Para ello creamos la funcion private fun createFile()
     */
    private fun generateUri() {
        uri = FileProvider.getUriForFile(
            Objects.requireNonNull(this),
            "com.example.firebasestorage.provider",
            createFile()
        )
    }

    /**
     * Creamos el fichero temporal con un nombre único.
     * Aqui usamos otra forma diferente para crear un string unico, a diferencia que el que hicimos
     * en TicTacToe que era -> Calendar.getInstance().timeInMillis.hashCode().toString()
     * Lo bueno de esta forma es que podemos organizar los archivos por la fecha
     */
    private fun createFile(): File {
        val name = SimpleDateFormat("yyyyMMdd_hhmmss").format(Date()) + "image"
        return File.createTempFile(name, ".jpg", externalCacheDir)
    }
}
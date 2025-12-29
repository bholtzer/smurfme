package com.bih.applicationsmurfforyou.presentation.smurfify


import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bih.applicationsmurfforyou.domain.util.ImageDownloader.bytesToBitmap
import com.bih.applicationsmurfforyou.domain.util.ImageDownloader.downloadImageBytes
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.ImagenForegroundMask
import com.google.firebase.ai.type.ImagenRawImage
import com.google.firebase.ai.type.PublicPreviewAPI
import com.google.firebase.ai.type.asImageOrNull
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.toImagenInlineImage
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class SmurfViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseStorage: FirebaseStorage,
    ) : ViewModel() {



    @OptIn(PublicPreviewAPI::class)
    private val imagenModel = Firebase.ai(backend = GenerativeBackend.vertexAI("us-central1"))
        .imagenModel("imagen-3.0-capability-001")

    val resultImage = MutableStateFlow<Bitmap?>(null)
    val isLoading = MutableStateFlow(false)

    private val _uiState = MutableStateFlow<SmurfifyUiState>(SmurfifyUiState.Idle)
    val uiState: StateFlow<SmurfifyUiState> = _uiState

    val body = """
    Transform the person into a Smurf cartoon style.
    Make the skin bright blue.
    Enlarge the head and eyes in classic Smurf proportions.
    Reduce body size and give a cartoon appearance.
    Preserve the person's facial expression and pose.
     
    """.trimIndent()


    val bodyy =  """
    A high-quality 3D digital animation style edit of the person in referenceImages [0]. 
    Transform the subject into a blue-skinned Smurf character. 
    Maintain the person's exact facial expression, eye shape, and smile from referenceImages[0], 
    but replace their skin with matte Smurf-blue. 
    Add a floppy white Smurf hat. 
    The final result should look like a character from a modern 3D Smurfs movie.
""".trimIndent()
    /*{
        "prompt": "Transform image into Smurf",
        "style": "cartoon"
    }
""".trimIndent()*/

    val prompt = "change me a smurf carton"

    fun onImageChosen(uri: Uri) {
        viewModelScope.launch {

            _uiState.value = SmurfifyUiState.Loading

            try {
                val imageUrl = uploadToFirebase(uri)
                transformToSmurf(imageUrl)
            } catch (e: Exception) {

                _uiState.value =
                    SmurfifyUiState.Error(e.message ?: "*****Smurfification failed")
            }
        }
    }

    private suspend fun uploadToFirebase(uri: Uri): String = withContext(Dispatchers.IO) {
        try {
            val storage = FirebaseStorage.getInstance().reference
            val fileName = "uploads/${UUID.randomUUID()}.jpg"
            val fileRef = storage.child(fileName)

            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw IllegalStateException("Cannot open input stream")

            fileRef.putStream(inputStream).await()
            inputStream.close()

            return@withContext fileRef.downloadUrl.await().toString()

        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    @OptIn(PublicPreviewAPI::class)
    suspend fun transformToSmurf(imageUrl: String) {

        _uiState.value = SmurfifyUiState.Loading

        val bytes = downloadImageBytes(imageUrl)
        //
        val sourceBitmap = bytesToBitmap(bytes)


        viewModelScope.launch {
            isLoading.value = true
            try {

                // Use the image-to-image API
                val response = imagenModel.editImage(
                    prompt = bodyy,
                    referenceImages = listOf(
                        // Wrap your bitmap in ImagenRawImage
                        ImagenRawImage(sourceBitmap.toImagenInlineImage()),
                        ImagenForegroundMask()
                    )
                )

                resultImage.value = response.images.firstOrNull()?.asBitmap()
                // Set the result to the first generated image

                _uiState.value = SmurfifyUiState.Success( resultImage.value)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading.value = false
            }
        }
    }

    suspend fun retakeSmurf(imageUrl: String) {
        return transformToSmurf(imageUrl)
    }

    suspend fun transformToSmurff(imageUrl: String): Bitmap? {

        _uiState.value = SmurfifyUiState.Loading

        val bytes = downloadImageBytes(imageUrl)
       //
         val bitmap = bytesToBitmap(bytes)

     //   val model: GenerativeModel = FirebaseAI.getInstance(context)
     //       .generativeModel("imagen-3.0-capability")

        //val model = Firebase.ai.generativeModel("imagen-3.0-capability")

        @OptIn(PublicPreviewAPI::class)
        val model = Firebase.ai(
            backend = GenerativeBackend.googleAI()
        ).generativeModel(
            modelName = "gemini-2.0-flash"
        )

        val prompt = """Transform the person in this photo into a cute Smurf-style cartoon character. " 
                "Make their skin Smurf-blue, give them a classic floppy white Smurf hat, " 
                "and use a high-quality 3D animation style similar to modern Smurf movies."""
            .trimIndent()

      //  return try {
        val response = model.generateContent(prompt)

            val bitmapp = response.candidates
                .firstOrNull()
                ?.content
                ?.parts
                ?.firstNotNullOfOrNull { it.asImageOrNull() }
                ?: error("No image returned from Gemini")

         //   val responseee =  model.generateContent(bitmap)
            _uiState.value = SmurfifyUiState.Success(bitmapp)

        return bitmap
            // Send the image + prompt to the AI
          /*  val response = model.generateImage(
                content {
                    image(inputBitmap)
                    text(prompt)
                }
            ) */
            // Return the first generated result
       //     response.images.firstOrNull()
       /* } catch (e: Exception) {
            e.printStackTrace()
            null
        }*/
    }



    @OptIn(PublicPreviewAPI::class)
    fun generateSmurf(imageUrl: String) {
        viewModelScope.launch {
            try {
                _uiState.value = SmurfifyUiState.Loading

                val bytes = downloadImageBytes(imageUrl)
                val bitmap = bytesToBitmap(bytes)

                val model = Firebase.ai(
                    backend = GenerativeBackend.googleAI()
                ).generativeModel(
                    modelName = "gemini-2.0-flash"
                )

                val prompt = content {
                    text(
                        """
                    Create a cute Smurf character.
                    Blue skin, white hat, cartoon style.
                    High quality illustration.
                    """
                    )
                }




                val response = model.generateContent(prompt)

                val imageBitmap = response.candidates
                    .firstOrNull()
                    ?.content
                    ?.parts
                    ?.firstNotNullOfOrNull { it.asImageOrNull() }
                    ?: throw IllegalStateException("No image returned")

                _uiState.value = SmurfifyUiState.Success(imageBitmap)



            } catch (e: Exception) {
                Log.e("SmurfifyViewModel", "generateSmurf error: ${e.message}")
                _uiState.value = SmurfifyUiState.Error(e.message ?: "Smurfify failed")

            }
        }
    }



}



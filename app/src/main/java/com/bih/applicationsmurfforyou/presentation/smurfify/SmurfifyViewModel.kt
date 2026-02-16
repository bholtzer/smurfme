package com.bih.applicationsmurfforyou.presentation.smurfify

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Tasks.await
import com.google.firebase.Firebase
import com.google.firebase.ai.ImagenModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.ImagePart
import com.google.firebase.ai.type.ImagenEditingConfig
import com.google.firebase.ai.type.ImagenGenerationResponse
import com.google.firebase.ai.type.ImagenInlineImage
import com.google.firebase.ai.type.ImagenSubjectReference
import com.google.firebase.ai.type.ImagenSubjectReferenceType
import com.google.firebase.ai.type.PublicPreviewAPI
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.asImageOrNull
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import com.google.firebase.ai.type.toImagenInlineImage
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.min

sealed class SmurfifyEvent {
    object ShowAd : SmurfifyEvent()
}

@HiltViewModel
class SmurfifyViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<SmurfifyUiState>(SmurfifyUiState.Idle)
    var uiState: StateFlow<SmurfifyUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<SmurfifyEvent>()
    val eventFlow: SharedFlow<SmurfifyEvent> = _eventFlow.asSharedFlow()

    private var chosenImageUri: Uri? = null

    // Step 1: User chooses an image.
    // We save the URI, trigger the ad, and start processing the image in the background.
    fun onImageChosen(uri: Uri) {
        chosenImageUri = uri
        // Start processing the image in the background.
        processSmurfImage()
        // Also trigger the ad to show.
        viewModelScope.launch {
            _eventFlow.emit(SmurfifyEvent.ShowAd)
        }
    }

    // Step 2: This function is now called immediately after an image is chosen.
    // It runs the image transformation in a background coroutine.
    @OptIn(PublicPreviewAPI::class)
    fun processSmurfImage() {
        val uri = chosenImageUri ?: return // Safety check

        viewModelScope.launch {
            try {
                _uiState.value = SmurfifyUiState.Loading

                val originalBitmap = uri.toBitmap(context)
                val scaledBitmap = originalBitmap.scale(1024)
                val imagenModel = Firebase.ai(backend = GenerativeBackend.vertexAI())
                    .imagenModel("imagen-2.0-edit-002")

                /*//val model = Firebase.ai(
               //     backend = GenerativeBackend.googleAI()
              //  ).generativeModel(
                    // --- MODEL FIX: Reverting to the latest documented model name ---
              //      modelName = "imagen-3.0-generate-002"
              //  )

                val prompt = content {
                    image(scaledBitmap)
                    text(
                        """
                        Your primary goal is to transform the person (or people) in this image into a cute, 3D animated Smurf character. The result should be a high-quality, photorealistic digital painting.

                        --- CRITICAL RULES ---
                        1.  **PRESERVE IDENTITY:** This is the most important rule. The Smurf's face MUST be a recognizable caricature of the person in the image. Keep their unique facial structure (face shape, mouth, nose, smile), gender, and approximate age. If there are accessories like glasses or beards, they MUST be included in the Smurf version.
                        2.  **HANDLE MULTIPLE PEOPLE:** If there is more than one person, transform EACH person into a unique Smurf, preserving their individual features.
                        3.  **CLASSIC SMURF LOOK:** Every character must have smooth, solid blue skin and a classic, large, white Smurf hat.

                        --- STYLE ---
                        - **Overall Style:** Modern 3D animation (like a character from a Pixar or Dreamworks movie), with a focus on being cute and appealing.
                        - **Eyes:** Transform the eyes to be large, white, and expressive in a classic cartoon style, but they should follow the original person's eye shape and position.
                        - **Background:** DO NOT change the original background of the image.
                        """
                    )
                }*/

               // val response = imagenModel.generateContent(prompt)

               /* val promptt = "A highly detailed, photo-realistic image of a lady smiling"
                val response = try await imagenModel.generateImages(prompt = promptt)


*/
               /* val response : ImagenGenerationResponse<ImagenInlineImage> =
                    customizeSmurfImage(imagenModel, scaledBitmap)
            //    val imagee = response.images.firstOrNull()?.asBitmap()

                val imageByteArray = response.images.firstOrNull()?.data
                    ?: throw IllegalStateException("No image data returned from model")

                val generatedBitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)

           */     /*val image = response.images.firstOrNull()?.apply {
                    this.asBitmap()
                }*/
//                    .firstOrNull()?.data
//                    ?.asBitmap()
//                    ?.parts
//                    ?.firstNotNullOfOrNull { it.asImageOrNull() }
//                    ?: throw IllegalStateException("No image returned from model")

                 val img = editImage(scaledBitmap)

                _uiState.value = SmurfifyUiState.Success(img)

            } catch (e: Exception) {
                Log.e("SmurfViewModel", "Smurfify error", e) // Improved logging
                _uiState.value = SmurfifyUiState.Error(e.message.toString())
            }
        }
    }
    object Gemini25FlashImagePreviewModelConfiguration {
        // [START android_gemini_developer_api_gemini_25_flash_image_model]
        val model = Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
            modelName = "gemini-2.5-flash-image",
            // Configure the model to respond with text and images (required)
            generationConfig = generationConfig {
                responseModalities = listOf(
                    ResponseModality.TEXT,
                    ResponseModality.IMAGE
                )
            }
        )
        // [END android_gemini_developer_api_gemini_25_flash_image_model]
    }


    suspend fun editImage(bitmap: Bitmap) : Bitmap? {
        val model = Gemini25FlashImagePreviewModelConfiguration.model

        val text3D ="Transform the person[1] in this photo into a 3D animated Smurf character." +
                " Blue skin, iconic white Smurf hat, white trousers, " +
                "standing in a mushroom village, vibrant colors, 4K, 3D render, style of Peyo."

        val text2D = "Edit this image to make it look like a smurf cartoon,Blue skin, Smurf hat, white trousers,style of Peyo"

        val prompt = content {
            image(bitmap)
            text(text2D)
        }

        // [START android_gemini_developer_api_edit_image]
       // scope.launch {
            // Provide a text prompt instructing the model to edit the image
          /*  val prompt = content {
                image(bitmap)
                text("Edit this image to make it look like a smurf cartoon,style of Peyo")
            }*/
            // To edit the image, call `generateContent` with the prompt (image and text input)
            val generatedImageAsBitmap: Bitmap? = model.generateContent(prompt)
                .candidates.first().content.parts.
                filterIsInstance<ImagePart>().firstOrNull()?.image
            // Handle the generated text and image
            return generatedImageAsBitmap
      //  }

        // [END android_gemini_developer_api_edit_image]
    }


    @OptIn(PublicPreviewAPI::class)
    suspend fun customizeSmurfImage(model: ImagenModel, referenceSumrfImage: Bitmap): ImagenGenerationResponse<ImagenInlineImage> {

        // Define the subject reference using the reference image.
        val subjectReference = ImagenSubjectReference(
            image = referenceSumrfImage.toImagenInlineImage(),
            referenceId = 1,
            description = "person",
            subjectType = ImagenSubjectReferenceType.PERSON
        )

        // Provide a prompt that describes the final image.
        // The "[1]" links the prompt to the subject reference with ID 1.
        val promptt = "A cat[1] flying through outer space"
        val prompt ="Transform the person[1] in this photo into a 3D animated Smurf character." +
                " Blue skin, iconic white Smurf hat, white trousers, " +
                "standing in a mushroom village, vibrant colors, 4K, 3D render, style of Peyo."
        // Use the editImage API to perform the subject customization.
        val editedImage = model.editImage(
            referenceImages = listOf(subjectReference),
            prompt = prompt,
            config = ImagenEditingConfig(
                editSteps = 50 // Number of editing steps, a higher value can improve quality
            )
        )

        return editedImage
    }
    private fun Uri.toBitmap(context: Context): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, this))
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, this)
        }
    }

    private fun Bitmap.scale(maxSize: Int): Bitmap {
        val originalWidth = this.width
        val originalHeight = this.height
        if (originalWidth <= maxSize && originalHeight <= maxSize) {
            return this
        }

        val scaleFactor = min(maxSize.toFloat() / originalWidth, maxSize.toFloat() / originalHeight)
        val newWidth = (originalWidth * scaleFactor).toInt()
        val newHeight = (originalHeight * scaleFactor).toInt()

        return Bitmap.createScaledBitmap(this, newWidth, newHeight, true)
    }
}

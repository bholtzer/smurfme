package com.bih.applicationsmurfforyou.presentation.smurfify

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bih.applicationsmurfforyou.data.repository.ImageRepository
import com.bih.applicationsmurfforyou.domain.repository.OpenAiRepository
import com.bih.applicationsmurfforyou.domain.util.Result
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
    private val aiRepository: OpenAiRepository,
    private val imageRepository : ImageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SmurfUiState>(SmurfUiState.Idle)
    val uiState: StateFlow<SmurfUiState> = _uiState

   // private val _smurfImage = MutableStateFlow<Result<Bitmap>>(Result.Idle)
 //   val smurfImage: StateFlow<Result<Bitmap>> = _smurfImage


    fun onImageChosen(uri: Uri) {
        viewModelScope.launch {

            _uiState.value = SmurfUiState.Loading

            try {
                val imageUrl = uploadToFirebase(uri)
                when (val result = imageRepository.createSmurfImage(imageUrl)) {


                    //          when (val result = aiRepository.smurfifyImage(imageUrl)) {

                    is Result.Success -> {
                        val result = aiRepository.smurfifyImage(imageUrl)
                        Log.d("OPENAI", "Smurfification result:Success")
                        _uiState.value = SmurfUiState.Success(result.toString())

                    }

                    is Result.Error -> {
                        Log.d("OPENAI", "Smurfification result: Error")

                        _uiState.value = SmurfUiState.Error(result.message)
                    }

                    else -> Unit
                }

            } catch (e: Exception) {
                Log.d("OPENAI", "Smurfification result: $e")
                _uiState.value =
                    SmurfUiState.Error(e.message ?: "*****Smurfification failed")
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
}



package com.bih.applicationsmurfforyou.presentation.smurfify

import android.Manifest
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
 import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
 import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
 import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.bih.applicationsmurfforyou.domain.util.Result
import org.w3c.dom.Text
import java.io.File


@Composable
fun SmurfScreen(
    viewModel: SmurfViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    val uiState = viewModel.uiState.collectAsState()
 //   val state = viewModel.smurfImage.collectAsState()

    val cameraImageUri = remember {
        val imagesDir = File(context.cacheDir, "images").apply { mkdirs() }
        val file = File(imagesDir, "capture_${System.currentTimeMillis()}.jpg")
        FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

    // Camera launcher
    val takePicture = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            /*      viewModel.generateSmurfFromPrompt(cameraImageUri)
        }*/
            viewModel.onImageChosen(cameraImageUri)

        }
    }
    // Gallery launcher
    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
      /*      viewModel.generateSmurfFromPrompt(it)
        }*/
             viewModel.onImageChosen(it) }
    }

    // Permission launcher for camera
    val cameraPermission = Manifest.permission.CAMERA
    val requestPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) takePicture.launch(cameraImageUri)
        else Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Smurfify Yourself!", style = MaterialTheme.typography.headlineMedium)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                pickImage.launch("image/*") }) {
                Text("Pick from Gallery") }

            Button(onClick = { requestPermission.launch(cameraPermission) }, modifier = Modifier.align(Alignment.Bottom)) {
                Text("Take Photo")
            }

        }

        Spacer(Modifier.height(16.dp))




//        when (val result = uiState.value) {
//
//            is SmurfUiState.Idle -> {
//                   Text("")
//            }
//            is SmurfUiState.Loading -> {
//                CircularProgressIndicator()
//                Text("Loading...")
//            }
//
//            is SmurfUiState.Error -> {
//                Text("Error: ${result.message}")
//            }
//
//            is SmurfUiState.Success -> {
//                //   SmurfImage(bitmap = result.data)
//                Text("Success!!! " + result.description)
//               /* val bitmap = (state as Result.Success<Text>).data
//                Image(
//                  //  bitmap = bitmap.asImageBitmap(),
//                    contentDescription = null,
//                    modifier = Modifier.fillMaxWidth()
//                )*/
//            }
//
//        }
    /*    when (val result = state.value) {

           *//* is Result.Loading -> {
                CircularProgressIndicator()
            }*//*

            is Result.Error -> {
                Text("Error: ${result.message}")
            }

            is Result.Success -> {
             //   SmurfImage(bitmap = result.data)
                Text("Success!!!")
                val bitmap = (state as Result.Success<Bitmap>).data
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth()
                )
            }

         //   is Result.Idle -> Text("Choose or take a photo to start!")
        }*/


    }
    Button(onClick = onBack)
    {
        Text("Back")
    }
}





@Composable
fun SmurfImage(bitmap: Bitmap) {
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "Smurf result",
        modifier = Modifier.fillMaxWidth()
    )
}

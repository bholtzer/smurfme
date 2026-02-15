package com.bih.applicationsmurfforyou.presentation.smurfify

import android.Manifest
import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.bih.applicationsmurfforyou.R
import com.bih.applicationsmurfforyou.presentation.ads.InterstitialAdManager
import com.bih.applicationsmurfforyou.presentation.composeable.LoadingState
import com.bih.applicationsmurfforyou.presentation.composeable.ui.theme.SmurfTheme
import java.io.File

@Composable
fun SmurfScreen(
    viewModel: SmurfifyViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    SmurfTheme {
        BackHandler(onBack = onBack)
        val context = LocalContext.current
        val activity = context as? Activity
        val uiState by viewModel.uiState.collectAsState()

        val adManager = remember { activity?.let { InterstitialAdManager(it) } }

        // Listen for events from the ViewModel to show the ad
        LaunchedEffect(Unit) {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    is SmurfifyEvent.ShowAd -> {
                        adManager?.showAd {
                            // This callback is guaranteed to run after the ad is dismissed or fails.
                            // Now, we start the image processing.
                            viewModel.processSmurfImage()
                        }
                    }
                }
            }
        }

        val cameraImageUri = remember {
            val imagesDir = File(context.cacheDir, "images").apply { mkdirs() }
            val file = File(imagesDir, "capture_${System.currentTimeMillis()}.jpg")
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        }

        val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                viewModel.onImageChosen(cameraImageUri)
            }
        }

        val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                viewModel.onImageChosen(it)
            }
        }

        val cameraPermissionDeniedText = stringResource(id = R.string.camera_permission_denied)
        val requestPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) takePicture.launch(cameraImageUri)
            else Toast.makeText(context, cameraPermissionDeniedText, Toast.LENGTH_SHORT).show()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = stringResource(id = R.string.smurfify_title),
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (val result = uiState) {
                    is SmurfifyUiState.Idle -> {
                        Text(stringResource(id = R.string.smurfify_prompt), style = MaterialTheme.typography.bodyLarge)
                    }
                    is SmurfifyUiState.Loading -> {
                        LoadingState(text = stringResource(id = R.string.smurfify_loading))
                    }
                    is SmurfifyUiState.Error -> {
                        Text(stringResource(id = R.string.smurfify_error, result.message), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.error)
                    }
                    is SmurfifyUiState.Success -> {
                        result.bitmap?.let { SmurfImage(it) }
                    }
                }
            }

            ActionButtons(
                isLoading = uiState is SmurfifyUiState.Loading,
                isImageLoaded = uiState is SmurfifyUiState.Success,
                onGalleryClick = { pickImage.launch("image/*") },
                onCameraClick = { requestPermission.launch(Manifest.permission.CAMERA) },
                onRefreshClick = { viewModel.processSmurfImage() } // Should re-process the last image
            )

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun ActionButtons(
    isLoading: Boolean,
    isImageLoaded: Boolean,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit,
    onRefreshClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = onGalleryClick, enabled = !isLoading) {
                Text(stringResource(id = R.string.button_pick_gallery))
            }
            Button(onClick = onCameraClick, enabled = !isLoading) {
                Text(stringResource(id = R.string.button_take_photo))
            }
        }
        if (isImageLoaded) { // Only show refresh if there's an image
            Spacer(modifier = Modifier.height(8.dp))
            IconButton(onClick = onRefreshClick, enabled = !isLoading) {
                Icon(Icons.Default.Refresh, contentDescription = stringResource(id = R.string.content_desc_refresh))
            }
        }
    }
}

@Composable
fun SmurfImage(bitmap: Bitmap) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = BorderStroke(4.dp, MaterialTheme.colorScheme.primary)
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = stringResource(id = R.string.content_desc_smurf_result),
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Fit
        )
    }
}

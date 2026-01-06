package com.bih.applicationsmurfforyou.presentation.smurfify

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.bih.applicationsmurfforyou.presentation.composeable.ui.theme.SmurfTheme
import java.io.File


@Composable
fun SmurfScreen(
    viewModel: SmurfViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    SmurfTheme {
        BackHandler(onBack = onBack)
        val context = LocalContext.current
        val uiState by viewModel.uiState.collectAsState()
        var lastImageUri by remember { mutableStateOf<Uri?>(null) }

        val cameraImageUri = remember {
            val imagesDir = File(context.cacheDir, "images").apply { mkdirs() }
            val file = File(imagesDir, "capture_${System.currentTimeMillis()}.jpg")
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        }

        val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                lastImageUri = cameraImageUri
                viewModel.onImageChosen(cameraImageUri)
            }
        }

        val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                lastImageUri = it
                viewModel.onImageChosen(it)
            }
        }

        val cameraPermission = Manifest.permission.CAMERA
        val requestPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) takePicture.launch(cameraImageUri)
            else Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
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
            Text(
                text = "Smurfify Yourself!",
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
                        Text("Choose a photo to begin your transformation!", style = MaterialTheme.typography.bodyLarge)
                    }
                    is SmurfifyUiState.Loading -> {
                        LoadingState()
                    }
                    is SmurfifyUiState.Error -> {
                        Text("Error: ${result.message}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.error)
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
                onCameraClick = { requestPermission.launch(cameraPermission) },
                onRefreshClick = { lastImageUri?.let { viewModel.onImageChosen(it) } }
            )
        }
    }
}

@Composable
fun LoadingState() {
    val alpha = remember { Animatable(0.5f) }
    LaunchedEffect(Unit) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(80.dp),
            strokeWidth = 8.dp,
            color = MaterialTheme.colorScheme.background
        )
        Text(
            text = "Smurfifying...",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.alpha(alpha.value)
        )
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
                Text("Pick from Gallery")
            }
            Button(onClick = onCameraClick, enabled = !isLoading) {
                Text("Take Photo")
            }
        }
        if (isImageLoaded) {
            Spacer(modifier = Modifier.height(8.dp))
            IconButton(onClick = onRefreshClick, enabled = !isLoading) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
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
            contentDescription = "Smurf result",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Fit
        )
    }
}

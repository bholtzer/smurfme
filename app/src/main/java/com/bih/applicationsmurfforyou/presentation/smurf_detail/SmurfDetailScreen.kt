package com.bih.applicationsmurfforyou.presentation.smurf_detail

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bih.applicationsmurfforyou.domain.model.Smurf
import com.bih.applicationsmurfforyou.presentation.composeable.ui.theme.SmurfTheme
import kotlinx.coroutines.delay

@Composable
fun SmurfDetailScreen(
    viewModel: SmurfDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    SmurfTheme {
        Box(
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
                ),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is SmurfDetailState.Loading -> {
                    CircularProgressIndicator()
                }
                is SmurfDetailState.Error -> {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
                is SmurfDetailState.Loaded -> {
                    SmurfDetailContent(
                        smurf = state.smurf,
                        isSpeaking = state.isSpeaking
                    )
                }
            }
        }
    }
}

@Composable
fun SmurfDetailContent(smurf: Smurf, isSpeaking: Boolean) {
    // --- Breathing/Talking Animation --- A more natural up-and-down motion
    val infiniteTransition = rememberInfiniteTransition(label = "Breathing Animation")
    val breathingOffsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Breathing Offset"
    )

    // --- Choreographed Dance Animation --- using a unified transition
    var isDancing by remember { mutableStateOf(false) }
    val danceTransition = updateTransition(targetState = isDancing, label = "Dance Transition")

    val translationY by danceTransition.animateFloat(
        transitionSpec = { keyframes { 
            durationMillis = 1200
            0f at 0 // Start
            50f at 200 // Crouch
            -150f at 600 // Jump
            0f at 900 // Land
        } },
        label = "Dance TranslationY"
    ) { dancing -> if (dancing) 0f else 0f }

    val rotationY by danceTransition.animateFloat(
        transitionSpec = { keyframes { 
            durationMillis = 1200
            0f at 200 // Start spinning after crouch
            360f at 800 // Complete spin in the air
        } },
        label = "Dance RotationY"
    ) { dancing -> if (dancing) 0f else 0f }

    val rotationZ by danceTransition.animateFloat(
        transitionSpec = { keyframes { 
            durationMillis = 1200
            0f at 900 // Start wobble on land
            10f at 1000
            -10f at 1100
            0f at 1200 // End wobble
        } },
        label = "Dance RotationZ"
    ) { dancing -> if (dancing) 0f else 0f }

    // This effect resets the animation state after it has finished playing.
    LaunchedEffect(isDancing) {
        if (isDancing) {
            delay(1200) // The total duration of the dance choreography
            isDancing = false
        }
    }


    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        smurf.imageUrl?.let {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(it)
                    .crossfade(true)
                    .build(),
                contentDescription = smurf.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(300.dp)
                    .graphicsLayer {
                        // Apply the choreographed dance animations
                        this.translationY = translationY
                        this.rotationY = rotationY
                        this.rotationZ = rotationZ

                        // Apply the breathing animation only when speaking
                        if (isSpeaking) {
                            this.translationY += breathingOffsetY
                        }
                    }
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                        if (!isDancing) { isDancing = true } // Trigger the animation
                    }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        smurf.name?.let {
            Text(text = it, style = MaterialTheme.typography.headlineLarge)
        }
    }
}

package com.bih.applicationsmurfforyou.presentation.smurf_detail

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import kotlinx.coroutines.launch

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
    // --- Breathing/Talking Animation --- a much more lifelike effect than simple scaling
    val infiniteTransition = rememberInfiniteTransition(label = "Breathing Animation")
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Breathing Scale"
    )

    // --- Choreographed Dance Animation States ---
    var isDancing by remember { mutableStateOf(false) }
    val rotationY = remember { Animatable(0f) }
    val translationY = remember { Animatable(0f) }
    val rotationZ = remember { Animatable(0f) }

    // This LaunchedEffect triggers the complex dance sequence when the user clicks.
    LaunchedEffect(isDancing) {
        if (isDancing) {
            launch {
                // 1. Crouch
                translationY.animateTo(50f, animationSpec = tween(200, easing = FastOutSlowInEasing))
                // 2. Jump
                translationY.animateTo(-150f, animationSpec = tween(400, easing = FastOutSlowInEasing))
                // 3. Land
                translationY.animateTo(0f, animationSpec = tween(300, easing = FastOutSlowInEasing))
            }
            launch {
                // 4. Spin while in the air
                delay(200) // Wait until the smurf has jumped
                rotationY.animateTo(360f, animationSpec = tween(600))
                rotationY.snapTo(0f) // Reset for the next dance
            }
            launch {
                // 5. Settle with a wobble after landing
                delay(900) // Wait until landing is complete
                rotationZ.animateTo(10f, animationSpec = tween(100))
                rotationZ.animateTo(-10f, animationSpec = tween(100))
                rotationZ.animateTo(0f, animationSpec = tween(100))

                isDancing = false // Animation complete, ready for next tap
            }
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
                        this.translationY = translationY.value
                        this.rotationY = rotationY.value
                        this.rotationZ = rotationZ.value

                        // Apply the breathing animation only when speaking
                        if (isSpeaking) {
                            this.scaleX = breathingScale
                            this.scaleY = breathingScale
                        }
                    }
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                        if (!isDancing) { // Prevent starting a new dance while one is in progress
                            isDancing = true
                        }
                    }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        smurf.name?.let {
            Text(text = it, style = MaterialTheme.typography.headlineLarge)
        }
    }
}

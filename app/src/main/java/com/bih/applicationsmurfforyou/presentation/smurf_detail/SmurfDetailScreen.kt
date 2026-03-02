package com.bih.applicationsmurfforyou.presentation.smurf_detail

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
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
    // --- Idle / Breathing Animation (Swaying and Scaling) ---
    val infiniteTransition = rememberInfiniteTransition(label = "Idle Animation")
    
    val breathingOffsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Breathing Y"
    )

    val idleSwayX by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Idle Sway X"
    )

    val scaleFactor by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Scale Factor"
    )

    // --- Choreographed Dance Animation ---
    var isDancing by remember { mutableStateOf(false) }
    val danceTransition = updateTransition(targetState = isDancing, label = "Dance Transition")

    val danceTranslationY by danceTransition.animateFloat(
        transitionSpec = { keyframes { 
            durationMillis = 1200
            0f at 0
            40f at 200 // Crouch
            -120f at 600 // Jump
            0f at 900 // Land
        } },
        label = "Dance Y"
    ) { dancing -> if (dancing) 0f else 0f }

    val danceTranslationX by danceTransition.animateFloat(
        transitionSpec = { keyframes { 
            durationMillis = 1200
            0f at 0
            -50f at 400
            50f at 800
            0f at 1200
        } },
        label = "Dance X"
    ) { dancing -> if (dancing) 0f else 0f }

    val rotationZ by danceTransition.animateFloat(
        transitionSpec = { keyframes { 
            durationMillis = 1200
            0f at 0
            -15f at 300
            15f at 900
            0f at 1200
        } },
        label = "Dance RotationZ"
    ) { dancing -> if (dancing) 0f else 0f }

    LaunchedEffect(isDancing) {
        if (isDancing) {
            delay(1200)
            isDancing = false
        }
    }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        smurf.imageUrl?.let {
            // We use a fixed-size clipped container (Card/Box) so the "frame" stays still
            // while the Smurf image moves internally, making it feel more like the character is moving.
            Card(
                modifier = Modifier
                    .size(320.dp)
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (!isDancing) isDancing = true
                    },
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(it)
                            .crossfade(true)
                            .build(),
                        contentDescription = smurf.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize(0.9f) // Leave some room for internal movement
                            .graphicsLayer {
                                // Apply combined translations
                                this.translationX = idleSwayX + danceTranslationX
                                this.translationY = breathingOffsetY + danceTranslationY
                                
                                // Apply rotations
                                this.rotationZ = rotationZ
                                
                                // Apply scaling for "breathing" effect
                                this.scaleX = scaleFactor
                                this.scaleY = scaleFactor

                                // If speaking, add extra emphasis
                                if (isSpeaking) {
                                    this.translationY -= 10f
                                }
                            }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        smurf.name?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

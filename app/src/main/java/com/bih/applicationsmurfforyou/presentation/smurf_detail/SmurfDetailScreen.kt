package com.bih.applicationsmurfforyou.presentation.smurf_detail

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bih.applicationsmurfforyou.presentation.composeable.ui.theme.SmurfTheme

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
fun SmurfDetailContent(smurf: com.bih.applicationsmurfforyou.domain.model.Smurf, isSpeaking: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "Speaking Animation")
    val speakingScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Speaking Scale"
    )

    var isWobbling by remember { mutableStateOf(false) }
    val wobbleRotation = remember { Animatable(0f) }

    LaunchedEffect(isWobbling) {
        if (isWobbling) {
            wobbleRotation.animateTo(15f, animationSpec = tween(150))
            wobbleRotation.animateTo(-15f, animationSpec = tween(150))
            wobbleRotation.animateTo(0f, animationSpec = tween(150))
            isWobbling = false
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
                    .scale(if (isSpeaking) speakingScale else 1f)
                    .graphicsLayer { rotationZ = wobbleRotation.value }
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                        isWobbling = true
                    }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        smurf.name?.let {
            Text(text = it, style = MaterialTheme.typography.headlineLarge)
        }
    }
}

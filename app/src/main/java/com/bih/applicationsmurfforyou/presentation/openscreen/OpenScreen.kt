package com.bih.applicationsmurfforyou.presentation.openscreen

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bih.applicationsmurfforyou.R

@Composable
fun OpenScreen(
    viewModel: OpenScreenViewModel = hiltViewModel(),
    onPreloadComplete: () -> Unit
) {
    val preloadState by viewModel.preloadState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(preloadState) {
        when (val state = preloadState) {
            is PreloadState.Success -> {
                onPreloadComplete()
            }
            is PreloadState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                onPreloadComplete()
            }
            else -> { /* Do nothing while Idle or Loading */ }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = R.drawable.open_screen),
            contentDescription = stringResource(id = R.string.content_desc_open_screen_bg),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        if (preloadState is PreloadState.Loading) {
            val progress = (preloadState as PreloadState.Loading).progress
            LoadingProgressIndicator(progress = progress / 100f)
        }
    }
}

@Composable
private fun LoadingProgressIndicator(progress: Float) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = "Preload Progress"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        CircularProgressIndicator(
            modifier = Modifier.size(100.dp),
            strokeWidth = 8.dp,
            color = MaterialTheme.colorScheme.onPrimary,
            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.loading_percentage, (animatedProgress * 100).toInt()),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

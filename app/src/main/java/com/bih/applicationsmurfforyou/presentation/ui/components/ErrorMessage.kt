package com.bih.applicationsmurfforyou.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bih.applicationsmurfforyou.R

@Preview(name = "Default Message")
@Composable
fun ErrorMessagePreview() {
    ErrorMessage()
}

@Preview(name = "Custom Message")
@Composable
fun ErrorMessageWithCustomMessagePreview() {
    ErrorMessage(message = "A custom error message.")
}

@Composable
fun ErrorMessage(message: String? = null) {
    val images = listOf(R.drawable.gargamel, R.drawable.azrael)
    // To select a random image without causing preview instability on recomposition,
    // we use `remember` to store the result of the random selection.
    // This ensures the image is chosen once and then remembered.
    val image = if (LocalInspectionMode.current) {
        // In preview mode, we select a fixed image to ensure stability.
        images.first()
    } else {
        // At runtime, we select a random image.
        remember { images.random() }
    }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = null,
            // Modifier.weight(1f) makes the image take up all available vertical space.
            // Modifier.fillMaxWidth() makes the image span the full width of the screen.
            // ContentScale.Crop scales the image to fill the bounds, cropping if necessary,
            // which is better for background images than ContentScale.Fit.
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
        Text(
            text = message ?: stringResource(id = R.string.no_internet_connection),
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

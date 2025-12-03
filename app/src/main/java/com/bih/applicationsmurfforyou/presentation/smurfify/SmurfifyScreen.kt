package com.bih.applicationsmurfforyou.presentation.smurfify

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SmurfifyScreen(
    viewModel: SmurfifyViewModel = hiltViewModel(),
    onBack: () -> Boolean
) {
    val uiState = viewModel.uiState

    var description by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Smurfify Yourself",
            style = MaterialTheme.typography.headlineMedium
        )





        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.createSmurf(description) },
            enabled = uiState !is SmurfifyUiState.Loading
        ) {
            Text("Create Smurf")
        }

        Spacer(modifier = Modifier.height(24.dp))

        when (uiState) {
            is SmurfifyUiState.Idle -> {
                Text("Describe yourself (or someone) and tap Create Smurf.")
            }

            is SmurfifyUiState.Loading -> {
                CircularProgressIndicator()
            }

            is SmurfifyUiState.Error -> {
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error
                )
            }

            is SmurfifyUiState.Success -> {
                SmurfImageView(bitmap = uiState.bitmap)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        BasicTextField(

            value = description,
            onValueChange = { description = it },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp),
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
        )
    }
}


@Composable
private fun SmurfImageView(bitmap: Bitmap) {
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "Smurfified image",
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    )
}

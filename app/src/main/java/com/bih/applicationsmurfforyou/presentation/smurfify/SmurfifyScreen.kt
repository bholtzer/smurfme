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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SmurfifyScreen(
    viewModel: SmurfifyViewModel = hiltViewModel(),
    onBack: () -> Boolean
) {
    //  val uiState = viewModel.uiState
    val uiState by viewModel.uiState.collectAsState()

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

        Spacer(modifier = Modifier.height(2.dp))
        Row {
            Button(
                onClick = {
                    onBack()
                }
            ) {
                Text("Back ")
            }

            Button(
                onClick = {
                    viewModel.generateSmurf(description)
                },
                enabled = uiState !is SmurfifyUiState.Loading
            ) {
                Text("Make me as a Smurf")
            }
            Spacer(modifier = Modifier.width(4.dp))
            Button(
                onClick = {
                    viewModel.generateSmurf(description)
                },
                enabled = uiState !is SmurfifyUiState.Loading
            ) {
                Text("Create Smurf")
            }
            Spacer(modifier = Modifier.width(4.dp))

        }
       // Spacer(modifier = Modifier.height(24.dp))

        when (uiState) {
            is SmurfifyUiState.Idle -> {
                Text("Describe yourself (or someone) and tap Create Smurf.")
            }

            is SmurfifyUiState.Loading -> {
                CircularProgressIndicator()
            }

            is SmurfifyUiState.Error -> {
                Text(
                    text = (uiState as SmurfifyUiState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }

            is SmurfifyUiState.Success -> {
                (uiState as SmurfifyUiState.Success).bitmap?.let { SmurfImageView(bitmap = it) }

            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))

    BasicTextField(
        value = description,
        onValueChange = { description = it },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 80.dp).background(color = Color.LightGray),
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
    )
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

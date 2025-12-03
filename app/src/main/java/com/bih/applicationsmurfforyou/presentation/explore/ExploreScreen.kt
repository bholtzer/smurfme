package com.bih.applicationsmurfforyou.presentation.explore

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bih.applicationsmurfforyou.presentation.ui.SmurfItem


@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel  = hiltViewModel(),
    onNavigate: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(Modifier.padding(16.dp).fillMaxWidth()) {
        Text("Explore Smurfs", style = MaterialTheme.typography.headlineMedium)

        Button(
            modifier = Modifier.align(Alignment.End),
            onClick = onNavigate
        ) { Text("Create New Smurf") }

        when (val state = uiState) {
            ExploreUiState.Idle -> Text("Welcome to SmurfLand!")
            ExploreUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            is ExploreUiState.Error -> Text(state.message, color = Color.Red)

            is ExploreUiState.Loaded -> {
                LazyColumn {
                    items(state.smurfs) { smurf ->
                        SmurfItem(smurf)
                    }
                }
            }
        }
    }
}

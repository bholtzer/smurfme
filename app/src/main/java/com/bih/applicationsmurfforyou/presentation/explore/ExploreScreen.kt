package com.bih.applicationsmurfforyou.presentation.explore

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.bih.applicationsmurfforyou.domain.model.Smurf
import com.bih.applicationsmurfforyou.presentation.composeable.ui.theme.SmurfTheme

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel = hiltViewModel(),
    onNavigateToSmurfify: () -> Unit,
    onSmurfClick: (String) -> Unit,
    onNavigateToLanguage: () -> Unit,
    onNavigateToPermissions: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToTerms: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    SmurfTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Smurf Village") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    actions = {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More options")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(text = { Text("Language") }, onClick = { showMenu = false; onNavigateToLanguage() })
                            DropdownMenuItem(text = { Text("Permissions") }, onClick = { showMenu = false; onNavigateToPermissions() })
                            DropdownMenuItem(text = { Text("Privacy Policy") }, onClick = { showMenu = false; onNavigateToPrivacy() })
                            DropdownMenuItem(text = { Text("Terms & Conditions") }, onClick = { showMenu = false; onNavigateToTerms() })
                        }
                    }
                )
            }
        ) { paddingValues ->
            val uiState by viewModel.uiState.collectAsState()
            val isRefreshing = (uiState as? ExploreUiState.Loaded)?.isRefreshing ?: false
            val pullRefreshState = rememberPullRefreshState(refreshing = isRefreshing, onRefresh = { viewModel.onRefresh() })

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .pullRefresh(pullRefreshState)
                ) {
                    when (val state = uiState) {
                        is ExploreUiState.Loading -> {
                            LoadingState(modifier = Modifier.align(Alignment.Center))
                        }
                        is ExploreUiState.Error -> {
                            Text(
                                text = "Error: ${state.message}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        is ExploreUiState.Loaded -> {
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(minSize = 128.dp),
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(8.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(state.smurfs) { character ->
                                    SmurfCharacterCard(character = character) {
                                        character.name?.let { onSmurfClick(it) }
                                    }
                                }
                            }
                        }
                        is ExploreUiState.Idle -> {}
                    }

                    PullRefreshIndicator(
                        refreshing = isRefreshing,
                        state = pullRefreshState,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onNavigateToSmurfify
                ) {
                    Text("Create Your Own Smurf")
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun SmurfCharacterCard(character: Smurf, onClick: () -> Unit) {
    Card(
        modifier = Modifier.clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(character.imageUrl)
                    .crossfade(true)
                    .size(256)
                    .build(),
                loading = {
                    CircularProgressIndicator(modifier = Modifier.padding(32.dp))
                },
                contentDescription = character.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.aspectRatio(1f)
            )
            character.name?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun LoadingState(modifier: Modifier = Modifier) {
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
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(80.dp),
            strokeWidth = 8.dp,
            color = MaterialTheme.colorScheme.background
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Loading Village...",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.alpha(alpha.value)
        )
    }
}

package com.bih.applicationsmurfforyou.presentation.explore

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.bih.applicationsmurfforyou.R
import com.bih.applicationsmurfforyou.data.util.ConnectivityObserver
import com.bih.applicationsmurfforyou.domain.model.Smurf
import com.bih.applicationsmurfforyou.presentation.composeable.LoadingState
import com.bih.applicationsmurfforyou.presentation.composeable.ui.theme.SmurfTheme
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
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
    val isGridLayout by viewModel.isGridLayout.collectAsState()
    val smurfUiState by viewModel.smurfUiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val networkStatus by viewModel.networkStatus.collectAsState()
    var smurfs by remember { mutableStateOf<List<Smurf>>(emptyList()) }

    // Persist the list of smurfs across refreshes
    LaunchedEffect(smurfUiState) {
        if (smurfUiState is SmurfUiState.Success) {
            smurfs = (smurfUiState as SmurfUiState.Success).smurfs
        }
    }

    SmurfTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(id = R.string.explore_title)) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    actions = {
                        IconButton(onClick = { viewModel.toggleLayout() }) {
                            Icon(
                                imageVector = if (isGridLayout) Icons.AutoMirrored.Filled.ViewList else Icons.Default.GridView,
                                contentDescription = stringResource(id = R.string.content_desc_change_layout)
                            )
                        }
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = stringResource(id = R.string.content_desc_more_options))
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.menu_language)) },
                                onClick = { showMenu = false; onNavigateToLanguage() },
                                leadingIcon = { Icon(Icons.Default.Language, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.menu_permissions)) },
                                onClick = { showMenu = false; onNavigateToPermissions() },
                                leadingIcon = { Icon(Icons.Default.Shield, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.menu_privacy_policy)) },
                                onClick = { showMenu = false; onNavigateToPrivacy() },
                                leadingIcon = { Icon(Icons.Default.PrivacyTip, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.menu_terms)) },
                                onClick = { showMenu = false; onNavigateToTerms() },
                                leadingIcon = { Icon(Icons.Filled.Description, contentDescription = null) }
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            val pullRefreshState = rememberPullToRefreshState()

            LaunchedEffect(pullRefreshState.isRefreshing) {
                if (pullRefreshState.isRefreshing) {
                    viewModel.loadSmurfs(forceRefresh = true)
                }
            }
            LaunchedEffect(isRefreshing) {
                if (!isRefreshing) {
                    pullRefreshState.endRefresh()
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
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
                            .nestedScroll(pullRefreshState.nestedScrollConnection)
                    ) {
                        if (smurfs.isNotEmpty()) {
                            if (isGridLayout) {
                                LazyVerticalGrid(
                                    columns = GridCells.Adaptive(minSize = 128.dp),
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(smurfs) { character ->
                                        SmurfCharacterCard(character = character, isOnline = networkStatus == ConnectivityObserver.Status.Available) {
                                            character.name?.let { onSmurfClick(it) }
                                        }
                                    }
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(vertical = 8.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(smurfs) { character ->
                                        SmurfCharacterListItem(character = character, isOnline = networkStatus == ConnectivityObserver.Status.Available) {
                                            character.name?.let { onSmurfClick(it) }
                                        }
                                    }
                                }
                            }
                        }

                        when (val state = smurfUiState) {
                            is SmurfUiState.Loading -> {
                                if (smurfs.isEmpty()) {
                                    LoadingState(modifier = Modifier.align(Alignment.Center), text = stringResource(id = R.string.explore_loading))
                                }
                            }
                            is SmurfUiState.Error -> {
                                GargamelError(message = state.message, showOverlay = smurfs.isNotEmpty())
                            }
                            is SmurfUiState.Success -> {
                                if (state.smurfs.isEmpty()) {
                                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text("No Smurfs in the village. Pull down to refresh.")
                                    }
                                }
                            }
                        }

                        PullToRefreshContainer(
                            state = pullRefreshState,
                            modifier = Modifier.align(Alignment.TopCenter)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onNavigateToSmurfify,
                        enabled = networkStatus == ConnectivityObserver.Status.Available
                    ) {
                        Text(stringResource(id = R.string.button_create_smurf))
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                }

                AnimatedVisibility(
                    visible = networkStatus != ConnectivityObserver.Status.Available,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it }),
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Text(
                        text = stringResource(id = R.string.no_internet_connection),
                        color = MaterialTheme.colorScheme.onError,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.error)
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun GargamelError(message: String, showOverlay: Boolean) {
    val villain = remember {
        try {
            if (Random.nextBoolean()) R.drawable.gargamel else R.drawable.azrael
        } catch (e: Exception) {
            R.drawable.gargamel // Fallback to gargamel if azrael is missing
        }
    }
    val backgroundModifier = if (showOverlay) {
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
    } else {
        Modifier.fillMaxSize()
    }

    Box(
        modifier = backgroundModifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = villain),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = if (showOverlay) 0.3f else 1f
        )
        Text(
            text = message,
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}


@Composable
fun SmurfCharacterListItem(character: Smurf, isOnline: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            SubcomposeAsyncImage(
                model = if (isOnline) {
                    ImageRequest.Builder(LocalContext.current)
                        .data(character.imageUrl)
                        .crossfade(true)
                        .build()
                } else null,
                loading = { CircularProgressIndicator() },
                contentDescription = character.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                character.name?.let {
                    Text(text = it, style = MaterialTheme.typography.titleLarge)
                }
                character.description?.let {
                    Text(text = it, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
                }
            }
        }
    }
}

@Composable
fun SmurfCharacterCard(character: Smurf, isOnline: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            SubcomposeAsyncImage(
                model = if (isOnline) {
                    ImageRequest.Builder(LocalContext.current)
                        .data(character.imageUrl)
                        .crossfade(true)
                        .size(256)
                        .build()
                } else null,
                loading = {
                    CircularProgressIndicator(modifier = Modifier.padding(32.dp))
                },
                contentDescription = character.name,
                contentScale = ContentScale.Fit,
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

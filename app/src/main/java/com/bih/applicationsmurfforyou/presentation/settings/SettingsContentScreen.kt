package com.bih.applicationsmurfforyou.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bih.applicationsmurfforyou.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyScreen(onBack: () -> Unit) {
    SettingsScaffold(stringResource(id = R.string.privacy_policy_title), onBack) {
        Text(stringResource(id = R.string.privacy_policy_content))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsScreen(onBack: () -> Unit) {
    SettingsScaffold(stringResource(id = R.string.terms_title), onBack) {
        Text(stringResource(id = R.string.terms_content))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionsScreen(onBack: () -> Unit) {
    SettingsScaffold(stringResource(id = R.string.permissions_title), onBack) {
        Text(stringResource(id = R.string.permissions_content))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScaffold(title: String, onBack: () -> Unit, content: @Composable () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.content_desc_back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            content()
        }
    }
}

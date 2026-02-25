package com.bih.applicationsmurfforyou.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bih.applicationsmurfforyou.R
import kotlin.random.Random

@Preview
@Composable
fun ErrorMessage() {
    val images = listOf(R.drawable.gargamel, R.drawable.azrael)
    val randomImage = images[Random.nextInt(images.size)]

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = randomImage),
            contentDescription = null,
            modifier = Modifier.weight(1f),
            contentScale = ContentScale.Fit)
        Text(text = stringResource(id = R.string.no_internet_connection))
    }
}
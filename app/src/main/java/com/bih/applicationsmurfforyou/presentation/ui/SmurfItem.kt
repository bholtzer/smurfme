package com.bih.applicationsmurfforyou.presentation.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bih.applicationsmurfforyou.domain.model.Smurf

@Composable
fun SmurfItem(smurf: Smurf) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
       // Log.d("SmurfURL", "smurf.imageUrl ${smurf.imageUrl}")

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
             //   .data(smurf.imageUrl)
                .data(smurf.image)
                .size(180)
                .crossfade(true)
                .build(),
            contentDescription = smurf.name,
            modifier = Modifier
                .size(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = smurf.name,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = smurf.description,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}
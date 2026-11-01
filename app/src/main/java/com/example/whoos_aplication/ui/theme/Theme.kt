package com.example.whoos_aplication.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun WhooshAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary          = WhooshRed,
            onPrimary        = Color.White,
            primaryContainer = WhooshLight,
            background       = Color.White,
            surface          = Color.White,
        ),
        content = content
    )
}
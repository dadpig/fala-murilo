package com.tairone.falamurilo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val colors = lightColorScheme(
    primary       = Color(0xFF6C63FF),
    secondary     = Color(0xFFFF6B6B),
    background    = Color(0xFFFDF8F0),
    surface       = Color.White,
    onPrimary     = Color.White,
    onBackground  = Color(0xFF1A1A1A),
    onSurface     = Color(0xFF1A1A1A),
)

@Composable
fun FalaMuriloTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = colors, content = content)
}

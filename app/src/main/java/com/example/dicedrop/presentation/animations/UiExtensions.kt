package com.example.dicedrop.presentation.animations

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

fun Modifier.shineEffect(offset: Float): Modifier = this.then(
    Modifier.drawWithContent {
        drawContent()

        val width = size.width
        val height = size.height

        val gradientWidth = width * 0.4f

        val startX = (offset * width) - gradientWidth
        val endX = startX + gradientWidth

        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.White.copy(alpha = 0.4f),
                    Color.Transparent
                ),
                start = Offset(startX, 0f),
                end = Offset(endX, height)
            )
        )
    }
)

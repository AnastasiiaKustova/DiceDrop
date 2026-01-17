package com.example.dicedrop.presentation.common

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit

@Composable
fun OutlinedText(
    modifier: Modifier = Modifier,
    text: String,
    fontWeight: FontWeight,
    fontSize: TextUnit,
    fillColor: Color,
    outlineColor: Color,
    outlineWidth: Float
) {
    Text(
        modifier = modifier,
        text = text,
        style = TextStyle(
            color = outlineColor,
            drawStyle = Stroke(
                width = outlineWidth,
            ),
            fontSize = fontSize,
            fontWeight = fontWeight
        )
    )
    Text(
        modifier = modifier,
        text = text,
        style = TextStyle(
            color = fillColor,
            drawStyle = Fill,
            fontSize = fontSize,
            fontWeight = fontWeight
        )
    )
}
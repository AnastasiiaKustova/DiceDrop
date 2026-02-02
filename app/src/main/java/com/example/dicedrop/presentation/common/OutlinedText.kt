package com.example.dicedrop.presentation.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun OutlinedText(
    modifier: Modifier = Modifier,
    text: String,
    fontWeight: FontWeight = FontWeight.Bold,
    fontSize: TextUnit = 20.sp,
    fillColor: Color = Color.White,
    outlineColor: Color = Color.Black,
    outlineWidth: Float = 5f
) {
    Box(modifier = modifier) {
        Text(
            textAlign = TextAlign.Center,
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
            textAlign = TextAlign.Center,
            text = text,
            style = TextStyle(
                color = fillColor,
                drawStyle = Fill,
                fontSize = fontSize,
                fontWeight = fontWeight
            )
        )
    }

}
package com.example.dicedrop.presentation.achievements.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.presentation.common.OutlinedText
import com.example.dicedrop.presentation.ui.theme.Blue2
import com.example.dicedrop.presentation.ui.theme.CellBG
import com.example.dicedrop.presentation.ui.theme.DarkBlue

@Composable
fun ContextWithProgress(
    modifier: Modifier = Modifier,
    current: Int,
    total: Int
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(30.dp),
        contentAlignment = Alignment.Center
    ) {

        LinearProgressIndicator(
            progress = { current.toFloat() / total },
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .clip(RoundedCornerShape(8.dp))
                /*.border(width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(CellBG, CellBG),
                        start = Offset(0f, 0f),
                        end = Offset(0f, Float.POSITIVE_INFINITY)
                    ),
                    shape = RoundedCornerShape(8.dp)
                )*/,
            color = CellBG,
            trackColor = Blue2
        )
        OutlinedText(
            text = "$current / $total",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            fillColor = Color.White,
            outlineColor = DarkBlue,
            outlineWidth = 3f
        )
    }
}
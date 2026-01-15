package com.example.dicedrop.presentation.game.ui.board.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GameCell(color: Color, size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            //.weight(1f)
            .padding(2.dp)
            .background(color, RoundedCornerShape(4.dp))
    )
}
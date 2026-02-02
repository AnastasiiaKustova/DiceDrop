package com.example.dicedrop.presentation.achievements.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.presentation.common.OutlinedText
import com.example.dicedrop.presentation.ui.theme.DarkBlue
import com.example.dicedrop.presentation.ui.theme.LightBlue
import com.example.dicedrop.presentation.ui.theme.White

@Composable
fun RowScope.IconWithCoinsCount(
    modifier: Modifier = Modifier,
    reward: Int,
    icCoinsId: Int,
) {
    Box(
        modifier = modifier
            .background(
                Brush.linearGradient(
                    colors = listOf(LightBlue, White),
                    start = Offset(0f, 0f),
                    end = Offset(0f, Float.POSITIVE_INFINITY)
                ), shape = RoundedCornerShape(12.dp)
            )
            .border(width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(White, LightBlue),
                    start = Offset(0f, 0f),
                    end = Offset(0f, Float.POSITIVE_INFINITY)
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .size(50.dp)
            .padding(10.dp),
    ) {
        Image(
            painter = painterResource(id = icCoinsId),
            contentDescription = "",
            modifier = Modifier
                .align(Alignment.Center)
                .padding(2.dp)
                .fillMaxSize()
        )
        OutlinedText(
            modifier = Modifier
                .align(alignment = Alignment.BottomEnd)
                .offset(x = 4.dp, y = 6.dp),
            fillColor = White,
            outlineColor = DarkBlue,
            outlineWidth = 5f,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            text = "$reward"
        )
    }
}
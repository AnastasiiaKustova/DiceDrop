package com.example.dicedrop.presentation.achievements.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.presentation.ui.theme.DarkBlue

@Composable
fun RowScope.AchievementTitle(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
) {
    Column(
        modifier = modifier
            .weight(1f)
            .padding(3.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            color = DarkBlue,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            text = title,
        )
        Text(
            color = Color.Black,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            text = description,
        )
    }
}
package com.example.dicedrop.presentation.achievements.animation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.dicedrop.presentation.common.ConfettiBurst

// Конфетти из кнопки "Забрать" на весь экран; касания не перехватывает, список можно листать
@Composable
fun ConfettiOverlay(
    data: RewardAnimationData?,
    onFinished: () -> Unit
) {
    if (data == null) return

    ConfettiBurst(
        key = data,
        modifier = Modifier.fillMaxSize(),
        count = 46,
        origin = data.start,
        onFinished = onFinished
    )
}

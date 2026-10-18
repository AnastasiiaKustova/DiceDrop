package com.example.dicedrop.presentation.common

import androidx.compose.runtime.staticCompositionLocalOf
import com.example.dicedrop.domain.feedback.BackgroundMusic
import com.example.dicedrop.domain.feedback.GameFeedback

// Звуки и вибрация доступны любому экрану без передачи через параметры.
// По умолчанию - заглушка, чтобы превью работали без DI
val LocalGameFeedback = staticCompositionLocalOf { GameFeedback.None }

val LocalBackgroundMusic = staticCompositionLocalOf { BackgroundMusic.None }

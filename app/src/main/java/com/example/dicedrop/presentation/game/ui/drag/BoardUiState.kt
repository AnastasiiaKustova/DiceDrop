package com.example.dicedrop.presentation.game.ui.drag

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp

class BoardUiState {
    var cellSize by mutableStateOf(32.dp)
    var boardOffsetX by mutableStateOf(0f)
    var boardOffsetY by mutableStateOf(0f)
}
package com.example.dicedrop.presentation.game.ui.drag

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp

class BoardUiState {
    // Под макет 390x844: поле 11x11 и блок 6x6 с линейками помещаются одновременно.
    // На планшете в альбомной ориентации клетка подбирается под экран
    var cellSize by mutableStateOf(PortraitCell)
    var boardOffsetX by mutableStateOf(0f)
    var boardOffsetY by mutableStateOf(0f)

    companion object {
        val PortraitCell = 29.dp
    }
}

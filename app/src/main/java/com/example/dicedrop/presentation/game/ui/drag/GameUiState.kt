package com.example.dicedrop.presentation.game.ui.drag

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.example.dicedrop.domain.entity.BoardItem
import com.example.dicedrop.domain.entity.DiceItem
import com.example.dicedrop.domain.entity.DraggingType
import com.example.dicedrop.presentation.game.ui.board.BoardUiHelper

class GameUiState(val draggingType: DraggingType) {

    var ghostRow by mutableStateOf<Int?>(null)
    var ghostCol by mutableStateOf<Int?>(null)

    var dragOffsetX by mutableStateOf(0.dp)
    var dragOffsetY by mutableStateOf(0.dp)

    var elementStartX by mutableStateOf(0f)
    var elementStartY by mutableStateOf(0f)

    var dragging by mutableStateOf(false)

    fun resetState() {
        dragging = false
        dragOffsetX = 0.dp
        dragOffsetY = 0.dp
        ghostRow = null
        ghostCol = null
    }

    fun canPlace(board: BoardItem, dice: DiceItem?): Boolean =
        BoardUiHelper.canPlace(board, ghostRow, ghostCol, dice)

    companion object {
        fun getList(): List<GameUiState> = DraggingType.entries.map { GameUiState(it) }

        fun List<GameUiState>.getGhost(): GameUiState?{
            return this.firstOrNull { it.ghostRow != null && it.ghostCol != null }
        }
    }
}
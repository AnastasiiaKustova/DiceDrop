package com.example.dicedrop.presentation.game.ui.drag

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.example.dicedrop.domain.entity.BoardItem
import com.example.dicedrop.domain.entity.DiceItem
import com.example.dicedrop.domain.entity.DraggingType
import com.example.dicedrop.domain.entity.GameActionType
import kotlin.math.roundToInt

object DragController {

    fun onDragStart(gameState: GameUiState) {
        gameState.dragging = true
    }

    fun onDragEnd(
        gameState: GameUiState,
        boardItem: BoardItem,
        diceItem: DiceItem? = null,
        // Блок отпустили там, где он не помещается, и он возвращается в лоток
        onReject: () -> Unit = {},
        onAction: (row: Int, col: Int, GameActionType) -> Unit
    ) {
        val row = gameState.ghostRow
        val col = gameState.ghostCol
        if (row == null || col == null) {
            onReject()
            return resetDragState(gameState)
        }

        when (gameState.draggingType) {
            DraggingType.BLOCK -> {
                if (gameState.canPlace(boardItem, diceItem)) {
                    onAction(row, col, GameActionType.PLACE_BLOCK)
                } else {
                    onReject()
                }
            }
        }

        resetDragState(gameState)
    }

    fun onDragCancel(gameState: GameUiState) {
        resetDragState(gameState)
    }

    fun onDrag(
        gameState: GameUiState,
        dragAmount: Offset,
        boardItem: BoardItem,
        board: BoardUiState,
        density: Density,
        speed: Float = 0.7f
    ) {
        gameState.dragOffsetX += (dragAmount.x * speed).dp
        gameState.dragOffsetY += (dragAmount.y * speed).dp

        val (row, col) = when (gameState.draggingType) {
            else -> calculateBlockTarget(gameState, board, density)
        }

        gameState.ghostRow = row
        gameState.ghostCol = col
    }

    private fun calculateBlockTarget(
        state: GameUiState,
        board: BoardUiState,
        density: Density
    ): Pair<Int?, Int?> {
        val col = ((state.elementStartX - board.boardOffsetX) /
                density.run { board.cellSize.toPx() }).roundToInt()
        val row = ((state.elementStartY - board.boardOffsetY) /
                density.run { board.cellSize.toPx() }).roundToInt()
        return row to col
    }

    private fun resetDragState(gameState: GameUiState) {
        gameState.resetState()
    }
}
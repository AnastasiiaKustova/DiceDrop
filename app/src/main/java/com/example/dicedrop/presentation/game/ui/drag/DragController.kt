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

    fun onDragStartWithOffset(gameState: GameUiState) {
        gameState.dragging = true
        gameState.dragOffsetY -= 100.dp
    }

    fun onDragEnd(
        gameState: GameUiState,
        boardItem: BoardItem,
        diceItem: DiceItem? = null,
        onAction: (row: Int, col: Int, GameActionType) -> Unit
    ) {
        val row = gameState.ghostRow ?: return resetDragState(gameState)
        val col = gameState.ghostCol ?: return resetDragState(gameState)

        when (gameState.draggingType) {
            DraggingType.BLOCK -> {
                if (gameState.canPlace(boardItem, diceItem)) {
                    onAction(row, col, GameActionType.PLACE_BLOCK)
                }
            }
            DraggingType.BOMB -> onAction(row, col, GameActionType.USE_BOMB)
            DraggingType.HAMMER -> onAction(row, col, GameActionType.USE_HAMMER)
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
            DraggingType.BOMB, DraggingType.HAMMER -> calculateBonusTarget(gameState, boardItem, board, density)
            else -> calculateBlockTarget(gameState, board, density)
        }

        gameState.ghostRow = row
        gameState.ghostCol = col
    }

    private fun calculateBonusTarget(
        state: GameUiState,
        boardItem: BoardItem,
        board: BoardUiState,
        density: Density
    ): Pair<Int?, Int?> {
        val col = ((state.elementStartX - board.boardOffsetX) /
                density.run { board.cellSize.toPx() }).roundToInt()
        val row = ((state.elementStartY - board.boardOffsetY) /
                density.run { board.cellSize.toPx() }).roundToInt()
        if (row !in 0 until boardItem.row || col !in 0 until boardItem.col) return null to null
        return row to col
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
package com.example.dicedrop.presentation.game.ui.board


import androidx.compose.ui.graphics.Color
import com.example.dicedrop.domain.engine.BoardEngine.ClearLines
import com.example.dicedrop.domain.engine.BoardEngine.canPlaceBlock
import com.example.dicedrop.domain.engine.BoardEngine.checkFullRows
import com.example.dicedrop.domain.engine.BoardEngine.placeBlock
import com.example.dicedrop.domain.entity.BoardItem
import com.example.dicedrop.domain.entity.DiceItem
import com.example.dicedrop.domain.entity.DraggingType
import com.example.dicedrop.presentation.game.ui.drag.GameUiState
import com.example.dicedrop.presentation.ui.theme.CellBG
import com.example.dicedrop.presentation.ui.theme.Green
import com.example.dicedrop.presentation.ui.theme.Red
import com.example.dicedrop.presentation.ui.theme.Yellow
import com.example.dicedrop.presentation.ui.theme.YellowDark

object BoardUiHelper {

    fun canPlace(
        board: BoardItem,
        row: Int?,
        col: Int?,
        dice: DiceItem?
    ): Boolean {
        return board.canPlaceBlock(
            row = row,
            col = col,
            h = dice?.dice1 ?: 0,
            w = dice?.dice2 ?: 0
        )
    }

    // Считается один раз на позицию призрака, а не для каждой клетки поля
    fun getClearLines(
        board: BoardItem,
        ghostRow: Int?,
        ghostCol: Int?,
        dice: DiceItem
    ): ClearLines {
        val empty = ClearLines(emptyList(), emptyList())
        if (dice == DiceItem.empty()) return empty
        if (ghostRow == null) return empty
        if (ghostCol == null) return empty
        if (!board.canPlaceBlock(ghostRow, ghostCol, dice.dice1, dice.dice2)) return empty
        val newBoard = board.placeBlock(ghostRow, ghostCol, dice.dice1, dice.dice2)
        return newBoard.checkFullRows()
    }

    fun getCellState(
        boardItem: BoardItem,
        dice: DiceItem,
        r: Int,
        c: Int,
        clearLines: ClearLines,
        isGhostValid: Boolean,
        uiState: GameUiState? = null
    ): CellState {

        val cellFilled = boardItem.board[r][c]
        val isInGhost = uiState?.isInGhost(dice, r, c) ?: false

        return when {
            r in clearLines.rows || c in clearLines.cols -> CellState.CAN_CLEAR
            cellFilled -> CellState.FILLED
            isInGhost && isGhostValid -> CellState.GHOST_VALID
            isInGhost -> CellState.GHOST_INVALID
            else -> CellState.EMPTY
        }
    }

    fun CellState.getCellColor(): Color = when (this) {
        CellState.FILLED -> Yellow
        CellState.GHOST_VALID -> Green
        CellState.GHOST_INVALID -> Red
        CellState.EMPTY -> CellBG
        CellState.CAN_CLEAR -> YellowDark
    }

    private fun GameUiState.isInGhost(dice: DiceItem, r: Int, c: Int): Boolean {
        val row = this.ghostRow ?: return false
        val col = this.ghostCol ?: return false
        return this.draggingType == DraggingType.BLOCK &&
                r in row until row + dice.dice1 &&
                c in col until col + dice.dice2
    }

    enum class CellState { FILLED, CAN_CLEAR, GHOST_VALID, GHOST_INVALID, EMPTY }
}
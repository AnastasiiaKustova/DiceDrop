package com.example.dicedrop.presentation.game.ui.board


import androidx.compose.ui.graphics.Color
import com.example.dicedrop.domain.engine.BoardEngine.canPlaceBlock
import com.example.dicedrop.domain.engine.BoardEngine.checkFullRows
import com.example.dicedrop.domain.engine.BoardEngine.placeBlock
import com.example.dicedrop.domain.entity.BoardItem
import com.example.dicedrop.domain.entity.DiceItem
import com.example.dicedrop.domain.entity.DraggingType
import com.example.dicedrop.presentation.game.ui.drag.GameUiState
import com.example.dicedrop.presentation.ui.theme.CellBG
import com.example.dicedrop.presentation.ui.theme.Green
import com.example.dicedrop.presentation.ui.theme.Orange
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

    fun canClear(
        board: BoardItem,
        row: Int,
        col: Int,
        ghostRow: Int?,
        ghostCol: Int?,
        dice: DiceItem
    ): Boolean {
        if (dice == DiceItem.empty()) return false
        if (ghostRow == null) return false
        if (ghostCol == null) return false
        if (!board.canPlaceBlock(ghostRow, ghostCol, dice.dice1, dice.dice2)) return false
        val newBoard = board.placeBlock(ghostRow, ghostCol, dice.dice1, dice.dice2)
        val (fullRows, fullCols) = newBoard.checkFullRows()
        if (fullRows.isEmpty() && fullCols.isEmpty()) return false
        return fullRows.firstOrNull { it == row } != null || fullCols.firstOrNull { it == col } != null
    }

    fun getCellState(
        boardItem: BoardItem,
        dice: DiceItem,
        r: Int,
        c: Int,
        uiState: GameUiState? = null
    ): CellState {

        val cellFilled = boardItem.board[r][c]
        val isInGhost = uiState?.isInGhost(dice, r, c) ?: false
        val isInBonusBomb = uiState?.isInBonusBomb(r, c) ?: false
        val isInBonusHammer = uiState?.isInBonusHammer(r, c) ?: false

        return when {
            isInBonusBomb || isInBonusHammer -> CellState.BONUS
            canClear(boardItem, r, c, uiState?.ghostRow, uiState?.ghostCol, dice) -> CellState.CAN_CLEAR
            cellFilled -> CellState.FILLED
            isInGhost && canPlace(
                boardItem,
                uiState?.ghostRow,
                uiState?.ghostCol,
                dice
            ) -> CellState.GHOST_VALID

            isInGhost -> CellState.GHOST_INVALID
            else -> CellState.EMPTY
        }
    }

    fun CellState.getCellColor(): Color = when (this) {
        CellState.BONUS -> Orange
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

    private fun GameUiState.isInBonusBomb(r: Int, c: Int): Boolean {
        val row = this.ghostRow ?: return false
        val col = this.ghostCol ?: return false
        return this.draggingType == DraggingType.BOMB &&
                ((r in (row - 10) until row + 10 && c == col) || r == row)
    }

    private fun GameUiState.isInBonusHammer(r: Int, c: Int): Boolean {
        val row = this.ghostRow ?: return false
        val col = this.ghostCol ?: return false
        return this.draggingType == DraggingType.HAMMER &&
                r in (row - 1)..(row + 1) && c in (col - 1)..(col + 1)
    }

    enum class CellState { FILLED, CAN_CLEAR, GHOST_VALID, GHOST_INVALID, BONUS, EMPTY }
}
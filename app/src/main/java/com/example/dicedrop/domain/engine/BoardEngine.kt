package com.example.dicedrop.domain.engine

import com.example.dicedrop.domain.entity.BoardItem
import kotlin.math.max
import kotlin.math.min

object BoardEngine {

    fun BoardItem.canPlaceBlock(row: Int?, col: Int?, h: Int, w: Int): Boolean {
        if (row == null || col == null) return false

        if (row < 0 || col < 0) return false

        if (row + h > board.size || col + w > board[0].size) return false
        for (r in row until row + h) {
            for (c in col until col + w) {
                if (board[r][c]) return false
            }
        }
        return true
    }

    fun BoardItem.isCanPlaceNewBlock(h: Int, w: Int): Boolean {

        for (r in 0..board.size - h) {
            for (c in 0..board[0].size - w) {
                if (canPlaceBlock(r, c, h, w))
                    return true
            }
        }
        return false
    }

    fun BoardItem.placeBlock(row: Int, col: Int, h: Int, w: Int): BoardItem {
        val newBoard = board.copy()

        for (r in row until row + h) {
            for (c in col until col + w) {
                newBoard[r][c] = true
            }
        }
        return copy(board = newBoard)
    }

    fun BoardItem.checkFullRows(): ClearLines {

        val rows = board.size
        val cols = board[0].size

        val rowsToClear: MutableList<Int> = mutableListOf()
        val colsToClear: MutableList<Int> = mutableListOf()


        for (r in 0 until rows) {
            if (board[r].all { it }) {
                rowsToClear.add(r)
            }
        }

        for (c in 0 until cols) {
            var isFull = true
            for (r in 0 until rows) {
                if (!board[r][c]) {
                    isFull = false
                    break
                }
            }
            if (isFull) colsToClear.add(c)
        }


        return ClearLines(rowsToClear.toList(),colsToClear.toList())
    }

    fun BoardItem.clearFullRows(rowsToClear: List<Int>, colsToClear: List<Int>): BoardItem {
        val newBoard = board.copy()
        for (r in newBoard.indices) {
            for (c in newBoard[r].indices) {
                if (r in rowsToClear || c in colsToClear) newBoard[r][c] = false
            }
        }
        return copy(board = newBoard)
    }

    private fun List<BooleanArray>.copy() = map { it.copyOf() }

    data class ClearLines(
        val rows: List<Int>,
        val cols: List<Int>
    )
}
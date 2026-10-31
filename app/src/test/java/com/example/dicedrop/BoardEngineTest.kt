package com.example.dicedrop

import com.example.dicedrop.domain.engine.BoardEngine.canPlaceBlock
import com.example.dicedrop.domain.engine.BoardEngine.checkFullRows
import com.example.dicedrop.domain.engine.BoardEngine.clearFullRows
import com.example.dicedrop.domain.engine.BoardEngine.isCanPlaceNewBlock
import com.example.dicedrop.domain.engine.BoardEngine.placeBlock
import com.example.dicedrop.domain.entity.BoardItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BoardEngineTest {
    private val empty = BoardItem.default()

    @Test
    fun `блок встаёт в пределах поля и не встаёт за краем`() {
        assertTrue(empty.canPlaceBlock(0, 0, 6, 6))
        assertTrue(empty.canPlaceBlock(5, 5, 6, 6))
        assertFalse(empty.canPlaceBlock(6, 5, 6, 6))
        assertFalse(empty.canPlaceBlock(5, 6, 6, 6))
        assertFalse(empty.canPlaceBlock(-1, 0, 1, 1))
        assertFalse(empty.canPlaceBlock(0, -1, 1, 1))
        assertFalse(empty.canPlaceBlock(null, 0, 1, 1))
        assertFalse(empty.canPlaceBlock(0, null, 1, 1))
    }

    @Test
    fun `блок не встаёт на занятую клетку`() {
        val board = empty.placeBlock(3, 3, 1, 1)
        assertFalse(board.canPlaceBlock(3, 3, 1, 1))
        assertFalse(board.canPlaceBlock(2, 2, 2, 2))
        assertTrue(board.canPlaceBlock(4, 4, 2, 2))
    }

    @Test
    fun `высота и ширина блока не перепутаны`() {
        val board = empty.placeBlock(0, 0, 2, 3)
        val filled = board.board.flatMapIndexed { r, line ->
            line.toList().mapIndexedNotNull { c, on -> if (on) r to c else null }
        }
        assertEquals(setOf(0 to 0, 0 to 1, 0 to 2, 1 to 0, 1 to 1, 1 to 2), filled.toSet())
    }

    @Test
    fun `установка и очистка не меняют исходное поле`() {
        val placed = empty.placeBlock(0, 0, 1, 1)
        assertFalse(empty.board[0][0])
        val cleared = placed.clearFullRows(listOf(0), emptyList())
        assertTrue(placed.board[0][0])
        assertFalse(cleared.board[0][0])
    }

    @Test
    fun `поиск места находит единственную дырку и понимает её размер`() {
        val hole = fullBoardExcept(10 to 10)
        assertTrue(hole.isCanPlaceNewBlock(1, 1))
        assertFalse(hole.isCanPlaceNewBlock(1, 2))
        assertFalse(hole.isCanPlaceNewBlock(2, 1))

        val wide = fullBoardExcept(4 to 4, 4 to 5, 4 to 6)
        assertTrue(wide.isCanPlaceNewBlock(1, 3))
        assertFalse(wide.isCanPlaceNewBlock(3, 1))
        assertFalse(fullBoardExcept().isCanPlaceNewBlock(1, 1))
    }

    @Test
    fun `блок больше поля просто не встаёт и не роняет игру`() {
        assertFalse(empty.isCanPlaceNewBlock(12, 1))
        assertFalse(empty.isCanPlaceNewBlock(1, 12))
    }

    @Test
    fun `полные строки и столбцы находятся одновременно`() {
        val full = (0 until 11).fold(empty) { b, i -> b.placeBlock(2, i, 1, 1) }
            .let { b -> (0 until 11).fold(b) { x, i -> x.placeBlock(i, 7, 1, 1) } }
        val lines = full.checkFullRows()
        assertEquals(listOf(2), lines.rows)
        assertEquals(listOf(7), lines.cols)

        val cleared = full.clearFullRows(lines.rows, lines.cols)
        assertTrue(cleared.board.all { row -> row.none { it } })
    }

    @Test
    fun `неполные линии не считаются`() {
        val lines = rowAlmostFull(0, freeCol = 10).checkFullRows()
        assertTrue(lines.rows.isEmpty())
        assertTrue(lines.cols.isEmpty())
    }
}

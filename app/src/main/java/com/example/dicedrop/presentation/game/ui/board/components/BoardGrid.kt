package com.example.dicedrop.presentation.game.ui.board.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable

@Composable
fun Grid(rows: Int, cols: Int, cellContent: @Composable (r: Int, c: Int) -> Unit) {
    Column {
        for (r in 0 until rows) {
            Row {
                for (c in 0 until cols) {
                    cellContent(r, c)
                }
            }
        }
    }
}
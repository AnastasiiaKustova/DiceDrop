package com.example.dicedrop.domain.entity

data class ClearLinesResult(
    val updatedGame: GameStatisticItem,
    val clearedLinesCount: Int = 0,
    val clearedRows: List<Int> = emptyList(),
    val clearedCols: List<Int> = emptyList(),
    val combo: Int = 0,
    val points: Int = 0,
    // Монеты за этот ход: за линии и за полную очистку поля
    val coins: Int = 0
)

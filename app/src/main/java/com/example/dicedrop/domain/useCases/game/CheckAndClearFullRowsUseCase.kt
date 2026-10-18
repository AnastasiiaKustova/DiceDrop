package com.example.dicedrop.domain.useCases.game

import com.example.dicedrop.domain.engine.BoardEngine.checkFullRows
import com.example.dicedrop.domain.engine.BoardEngine.clearFullRows
import com.example.dicedrop.domain.entity.ClearLinesResult
import com.example.dicedrop.domain.entity.GameStatisticItem
import com.example.dicedrop.domain.repository.GameRepository
import javax.inject.Inject

class CheckAndClearFullRowsUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(game: GameStatisticItem): ClearLinesResult {
        val (rows, cols) = game.boardItem.checkFullRows()
        // Ход без очистки сбрасывает комбо
        if (rows.isEmpty() && cols.isEmpty()) {
            if (game.combo == 0) return ClearLinesResult(game)
            val reset = game.copy(combo = 0)
            repository.updateGameState(reset)
            return ClearLinesResult(reset)
        }

        val clearedBoard = game.boardItem.clearFullRows(rows, cols)
        val count = (rows.size + cols.size)
        val combo = game.combo + count
        val delta = count * 100 * combo

        val updated = game.copy(
            boardItem = clearedBoard,
            combo = combo,
            totalScore = game.totalScore + delta,
            bestScore = if (game.isRecordAchieved) game.totalScore + delta else game.bestScore,
            isNewRecord = game.totalScore + delta > game.bestScore && game.bestScore > 0 && !game.isRecordAchieved
        )

        repository.updateGameState(updated)

        return ClearLinesResult(
            updatedGame = updated,
            clearedLinesCount = count,
            clearedRows = rows,
            clearedCols = cols,
            combo = combo,
            points = delta)
    }
}
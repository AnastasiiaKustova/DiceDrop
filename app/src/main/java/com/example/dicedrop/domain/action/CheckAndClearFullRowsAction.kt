package com.example.dicedrop.domain.action

import com.example.dicedrop.domain.engine.AchievementTracker
import com.example.dicedrop.domain.entity.ClearLinesResult
import com.example.dicedrop.domain.entity.GameStatisticItem
import com.example.dicedrop.domain.entity.statistic.PlayerStat
import com.example.dicedrop.domain.useCases.game.CheckAndClearFullRowsUseCase
import javax.inject.Inject

class CheckAndClearFullRowsAction @Inject constructor(
    private val checkAndClearFullRowsUseCase: CheckAndClearFullRowsUseCase,
    private val achievementTracker: AchievementTracker
) {
    suspend operator fun invoke(game: GameStatisticItem): ClearLinesResult {
        val clearedResult = checkAndClearFullRowsUseCase(game)

        if (clearedResult.clearedLinesCount > 0) {
            val count = clearedResult.clearedLinesCount.toLong()
            val updatedGame = clearedResult.updatedGame

            achievementTracker.increment(PlayerStat.LINES_CLEARED, count)
            achievementTracker.updateMax(PlayerStat.MAX_LINES_AT_ONCE, count)
            achievementTracker.updateMax(PlayerStat.BEST_GAME_SCORE, updatedGame.totalScore.toLong())
            achievementTracker.updateMax(PlayerStat.MAX_COMBO, clearedResult.combo.toLong())

            val isBoardEmpty = updatedGame.boardItem.board.all { row -> row.none { it } }
            if (isBoardEmpty) achievementTracker.increment(PlayerStat.BOARD_CLEARED)
        }

        return clearedResult
    }
}

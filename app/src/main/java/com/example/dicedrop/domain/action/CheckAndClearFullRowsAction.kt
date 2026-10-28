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
            achievementTracker.updateMax(PlayerStat.MAX_BOARD_CLEARS_IN_GAME, updatedGame.boardClears.toLong())

            // Линию закрыл блок 1×1: он занимает ровно одну клетку
            if (game.lastPlaced.size == 1) achievementTracker.increment(PlayerStat.LINES_BY_TINY_BLOCK)
            if (updatedGame.usedBonuses.isEmpty()) {
                achievementTracker.updateMax(PlayerStat.BEST_SCORE_NO_BONUS, updatedGame.totalScore.toLong())
            }
        }

        return clearedResult
    }
}

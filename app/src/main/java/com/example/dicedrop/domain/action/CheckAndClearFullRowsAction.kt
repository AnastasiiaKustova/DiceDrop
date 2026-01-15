package com.example.dicedrop.domain.action

import com.example.dicedrop.domain.engine.AchievementTracker
import com.example.dicedrop.domain.entity.ClearLinesResult
import com.example.dicedrop.domain.entity.GameStatisticItem
import com.example.dicedrop.domain.entity.statistic.PlayerStat
import com.example.dicedrop.domain.repository.PlayerStatsRepository
import com.example.dicedrop.domain.useCases.game.CheckAndClearFullRowsUseCase
import javax.inject.Inject

class CheckAndClearFullRowsAction @Inject constructor(
    private val checkAndClearFullRowsUseCase: CheckAndClearFullRowsUseCase,
    private val playerStatsRepository: PlayerStatsRepository,
    private val achievementTracker: AchievementTracker
) {
    suspend operator fun invoke(game: GameStatisticItem): ClearLinesResult {
        val clearedResult = checkAndClearFullRowsUseCase(game)

        if (clearedResult.clearedLinesCount > 0) {
            playerStatsRepository.increment(
                PlayerStat.LINES_CLEARED,
                clearedResult.clearedLinesCount.toLong()
            )
            achievementTracker.onClearRows()
        }

        return clearedResult
    }
}
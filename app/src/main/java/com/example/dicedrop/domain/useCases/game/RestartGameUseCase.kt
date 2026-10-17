package com.example.dicedrop.domain.useCases.game

import com.example.dicedrop.domain.engine.AchievementTracker
import com.example.dicedrop.domain.entity.GameStatisticItem
import com.example.dicedrop.domain.entity.statistic.PlayerStat
import com.example.dicedrop.domain.repository.GameRepository
import javax.inject.Inject

class RestartGameUseCase @Inject constructor(
    private val repository: GameRepository,
    private val achievementTracker: AchievementTracker
) {
    suspend operator fun invoke() {
        val currentGameStatistic = repository.getCurrentGame().run {
            if (totalScore > bestScore) {
                copy(bestScore = totalScore)
            } else {
                this
            }
        }

        // Партия засчитывается, только если в ней поставили хотя бы один блок,
        // иначе достижение можно накрутить кнопкой рестарта
        val wasPlayed = currentGameStatistic.reviveSnapshot != null || currentGameStatistic.totalScore > 0

        val newGame = GameStatisticItem.default().copy(
            bestScore = currentGameStatistic.bestScore,
            coins = currentGameStatistic.coins,
            bonuses = currentGameStatistic.bonuses
        )

        repository.saveGame(newGame)

        if (wasPlayed) achievementTracker.increment(PlayerStat.GAMES_PLAYED)
    }
}

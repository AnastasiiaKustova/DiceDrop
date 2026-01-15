package com.example.dicedrop.domain.useCases.game

import com.example.dicedrop.domain.entity.GameStatisticItem
import com.example.dicedrop.domain.repository.GameRepository
import javax.inject.Inject

class RestartGameUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke() {
        val currentGameStatistic = repository.getCurrentGame().run {
            if (totalScore > bestScore) {
                copy(bestScore = totalScore)
            } else {
                this
            }
        }

        val newGame = GameStatisticItem.default().copy(
            bestScore = currentGameStatistic.bestScore,
            coins = currentGameStatistic.coins,
            bonuses = currentGameStatistic.bonuses
        )

        repository.saveGame(newGame)
    }
}
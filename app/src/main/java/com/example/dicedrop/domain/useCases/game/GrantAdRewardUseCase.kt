package com.example.dicedrop.domain.useCases.game

import com.example.dicedrop.domain.entity.DiceItem
import com.example.dicedrop.domain.entity.TrayState
import com.example.dicedrop.domain.repository.GameRepository
import javax.inject.Inject

class GrantAdRewardUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke() {
        val gameStatistic = repository.getCurrentGame()
        repository.updateGameState(
            gameStatistic.copy(
                dice = DiceItem.empty(),
                tray = TrayState(),
                isGameOver = false
            )
        )
    }
}
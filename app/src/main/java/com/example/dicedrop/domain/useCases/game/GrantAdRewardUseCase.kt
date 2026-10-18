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
        val snapshot = gameStatistic.reviveSnapshot

        // Есть снимок - откатываем последний ход, нет - просто перебрасываем кубики
        val revived = if (snapshot != null) {
            gameStatistic.copy(
                boardItem = snapshot.boardItem,
                dice = snapshot.dice,
                tray = snapshot.tray,
                totalScore = snapshot.totalScore,
                combo = snapshot.combo,
                lastPlaced = emptyList(),
                reviveSnapshot = null,
                isGameOver = false
            )
        } else {
            gameStatistic.copy(
                dice = DiceItem.empty(),
                tray = TrayState(),
                isGameOver = false
            )
        }

        repository.saveGame(revived)
    }
}
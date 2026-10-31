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
                boardClears = snapshot.boardClears,
                // Монеты за отменённый ход забираем, иначе тот же ход можно оплачивать снова и снова
                coins = (gameStatistic.coins - snapshot.moveCoins).coerceAtLeast(0),
                coinsEarned = (gameStatistic.coinsEarned - snapshot.moveCoins).coerceAtLeast(0),
                lastPlaced = emptyList(),
                reviveSnapshot = null,
                isGameOver = false,
                moves = (gameStatistic.moves - 1).coerceAtLeast(0),
                movesAfterRevive = 0
            )
        } else {
            gameStatistic.copy(
                dice = DiceItem.empty(),
                tray = TrayState(),
                isGameOver = false,
                movesAfterRevive = 0
            )
        }

        repository.saveGame(revived)
    }
}
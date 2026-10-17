package com.example.dicedrop.domain.useCases.game

import com.example.dicedrop.domain.engine.AchievementTracker
import com.example.dicedrop.domain.engine.BoardEngine.canPlaceBlock
import com.example.dicedrop.domain.engine.BoardEngine.placeBlock
import com.example.dicedrop.domain.entity.DiceItem
import com.example.dicedrop.domain.entity.GameStatisticItem
import com.example.dicedrop.domain.entity.ReviveSnapshot
import com.example.dicedrop.domain.entity.statistic.PlayerStat
import com.example.dicedrop.domain.repository.GameRepository
import javax.inject.Inject

class PlaceBlockUseCase @Inject constructor(
    private val repository: GameRepository,
    private val achievementTracker: AchievementTracker
) {
    suspend operator fun invoke(row: Int, col: Int): GameStatisticItem? {
        val current = repository.getCurrentGame()
        val h = current.dice.dice1
        val w = current.dice.dice2

        // Пока третий кубик не сброшен, ставить блок нельзя
        if (current.tray.spare != null) return null
        if (!current.boardItem.canPlaceBlock(row = row, col = col, h = h, w = w)) return null

        val placedBoard = current.boardItem.placeBlock(row = row, col = col, h = h, w = w)

        val cells = buildList {
            for (r in row until row + h) for (c in col until col + w) add(r * current.boardItem.col + c)
        }
        // Кубики очищаем сразу, чтобы блок не возвращался в лоток после установки
        val updated = current.copy(
            boardItem = placedBoard,
            dice = DiceItem.empty(),
            lastPlaced = cells,
            reviveSnapshot = ReviveSnapshot(
                boardItem = current.boardItem,
                dice = current.dice,
                tray = current.tray,
                totalScore = current.totalScore,
                combo = current.combo
            ))
        repository.updateGameState(updated)
        achievementTracker.updateMax(PlayerStat.MAX_BLOCK_AREA, (h * w).toLong())
        return updated
    }
}
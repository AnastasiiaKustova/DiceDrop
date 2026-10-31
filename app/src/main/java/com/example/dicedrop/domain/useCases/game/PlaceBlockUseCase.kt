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
        // Блока нет: пустой блок «встал» бы куда угодно и потратил ход
        if (h <= 0 || w <= 0) return null
        if (!current.boardItem.canPlaceBlock(row = row, col = col, h = h, w = w)) return null

        val placedBoard = current.boardItem.placeBlock(row = row, col = col, h = h, w = w)

        val cells = buildList {
            for (r in row until row + h) for (c in col until col + w) add(r * current.boardItem.col + c)
        }
        // «Второй шанс»: третий ход после отмены за рекламу засчитывается один раз
        val movesAfterRevive = current.movesAfterRevive?.plus(1)
        val revivalSurvived = movesAfterRevive != null && movesAfterRevive >= REVIVE_MOVES_TO_SURVIVE

        // Кубики очищаем сразу, чтобы блок не возвращался в лоток после установки
        val updated = current.copy(
            boardItem = placedBoard,
            dice = DiceItem.empty(),
            lastPlaced = cells,
            moves = current.moves + 1,
            movesAfterRevive = if (revivalSurvived) null else movesAfterRevive,
            reviveSnapshot = ReviveSnapshot(
                boardItem = current.boardItem,
                dice = current.dice,
                tray = current.tray,
                boardClears = current.boardClears,
                totalScore = current.totalScore,
                combo = current.combo
            ))
        repository.updateGameState(updated)
        achievementTracker.updateMax(PlayerStat.MAX_BLOCK_AREA, (h * w).toLong())
        if (h * w == 1) achievementTracker.increment(PlayerStat.TINY_BLOCKS_PLACED)
        achievementTracker.updateMax(PlayerStat.MAX_MOVES_IN_GAME, updated.moves.toLong())
        if (revivalSurvived) achievementTracker.increment(PlayerStat.REVIVES_SURVIVED)
        return updated
    }

    private companion object {
        const val REVIVE_MOVES_TO_SURVIVE = 3
    }
}
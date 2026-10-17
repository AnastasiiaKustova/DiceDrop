package com.example.dicedrop.domain.useCases.game

import com.example.dicedrop.domain.engine.BoardEngine.placeBlock
import com.example.dicedrop.domain.entity.DiceItem
import com.example.dicedrop.domain.entity.GameStatisticItem
import com.example.dicedrop.domain.repository.GameRepository
import javax.inject.Inject

class PlaceBlockUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(row: Int, col: Int): GameStatisticItem {
        val current = repository.getCurrentGame()
        val h = current.dice.dice1
        val w = current.dice.dice2
        val placedBoard = current.boardItem.placeBlock(row = row, col = col, h = h, w = w)

        val cells = buildList {
            for (r in row until row + h) for (c in col until col + w) add(r * current.boardItem.col + c)
        }
        val updated = current.copy(
            boardItem = placedBoard,
            lastPlaced = cells,)
        repository.updateGameState(updated)
        return updated
    }

    suspend fun markThawing() {
        val current = repository.getCurrentGame()
        val a = current.tray.slotA
        val b = current.tray.slotB
        if (a?.frozen != true && b?.frozen != true) return
        repository.updateGameState(
            current.copy(
                tray = current.tray.copy(
                    slotA = a?.copy(thawing = true),
                    slotB = b?.copy(thawing = true)
                )
            )
        )
    }

    suspend fun clearDice() {
        val current = repository.getCurrentGame()
        repository.updateGameState(current.copy(dice = DiceItem.empty()))
    }
}
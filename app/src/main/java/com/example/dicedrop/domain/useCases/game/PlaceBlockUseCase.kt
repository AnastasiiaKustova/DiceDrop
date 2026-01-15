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
        val placedBoard = current.boardItem.placeBlock(
            row = row,
            col = col,
            h = current.dice.dice1,
            w = current.dice.dice2
        )

        val updated = current.copy(
            boardItem = placedBoard,
            dice = DiceItem.empty()
        )
        repository.updateGameState(updated)
        return updated
    }
}
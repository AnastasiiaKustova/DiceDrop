package com.example.dicedrop.domain.useCases.game

import com.example.dicedrop.domain.engine.BoardEngine.isCanPlaceNewBlock
import com.example.dicedrop.domain.entity.GameStatisticItem
import com.example.dicedrop.domain.repository.GameRepository
import javax.inject.Inject

class CheckGameOverUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(): GameStatisticItem {

        val game = repository.getCurrentGame()

        //TODO проверки на наличие бонусов?
        val canPlace = game.boardItem.isCanPlaceNewBlock(
            h = game.dice.dice1,
            w = game.dice.dice2
        )

        val updated = game.copy(isGameOver = !canPlace)

        repository.saveGame(updated)

        return updated
    }
}
package com.example.dicedrop.domain.useCases.game

import com.example.dicedrop.domain.entity.DiceItem
import com.example.dicedrop.domain.repository.GameRepository
import javax.inject.Inject

class RollDicesUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(result: DiceItem? = null): DiceItem {

        val currentGame = repository.getCurrentGame()
        val dices = result ?: DiceItem.randomDiceItem()

        val updatedGame = currentGame.copy(
            dice = dices
        )
        repository.updateGameState(updatedGame)

        return dices
    }
}
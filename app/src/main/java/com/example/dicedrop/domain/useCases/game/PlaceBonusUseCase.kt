package com.example.dicedrop.domain.useCases.game

import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.domain.repository.GameRepository
import javax.inject.Inject

class PlaceBonusUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(row: Int, col: Int, bonusType: BonusType) {
        val current = repository.getCurrentGame()

        val clearedBoard = when (bonusType){
            else -> current.boardItem
        }

        val updated = current.copy(
            boardItem = clearedBoard
        )
        repository.updateGameState(updated)
    }
}
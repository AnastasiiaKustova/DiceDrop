package com.example.dicedrop.domain.useCases.game

import com.example.dicedrop.domain.engine.BoardEngine.clearAreaBomb
import com.example.dicedrop.domain.engine.BoardEngine.clearAreaHammer
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.domain.repository.GameRepository
import javax.inject.Inject

class PlaceBonusUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(row: Int, col: Int, bonusType: BonusType) {
        val current = repository.getCurrentGame()

        val clearedBoard = when (bonusType){
            BonusType.BOMB -> current.boardItem.clearAreaBomb(row, col)
            BonusType.HAMMER -> current.boardItem.clearAreaHammer(row, col)
            else -> current.boardItem
        }

        val updated = current.copy(
            boardItem = clearedBoard
        )
        repository.updateGameState(updated)
    }
}
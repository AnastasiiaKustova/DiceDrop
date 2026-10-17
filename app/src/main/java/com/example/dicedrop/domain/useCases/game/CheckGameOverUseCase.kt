package com.example.dicedrop.domain.useCases.game

import com.example.dicedrop.domain.engine.BoardEngine.isCanPlaceNewBlock
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.domain.entity.GameStatisticItem
import com.example.dicedrop.domain.repository.GameRepository
import javax.inject.Inject

class CheckGameOverUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(): GameStatisticItem {
        val game = repository.getCurrentGame()

        val h = game.dice.dice1
        val w = game.dice.dice2
        fun count(t: BonusType) = game.bonuses.firstOrNull { it.type == t }?.count ?: 0
        val fitsDirect = game.boardItem.isCanPlaceNewBlock(h, w)
        val fitsSwapped = count(BonusType.ROTATE) > 0 && game.boardItem.isCanPlaceNewBlock(w, h)
        val canPlace = fitsDirect || fitsSwapped

        val hasRoom = game.boardItem.board.any { row -> row.any { !it } }
        val canSurvive = hasRoom && (count(BonusType.DRAFT) > 0 || count(BonusType.LUCKY) > 0)

        val updated = if (canPlace) {
            game.copy(isGameOver = false)
        } else {
            game.copy(isGameOver = !canSurvive)
        }

        repository.saveGame(updated)
        return updated
    }
}
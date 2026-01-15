package com.example.dicedrop.domain.useCases.game

import com.example.dicedrop.core.NotEnoughBonusException
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.domain.entity.DiceItem
import com.example.dicedrop.domain.entity.DiceItem.Companion.rotateDices
import com.example.dicedrop.domain.entity.GameStatisticItem
import com.example.dicedrop.domain.repository.GameRepository
import javax.inject.Inject

class UseBonusUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(bonusType: BonusType) {
        val gameStatistic = repository.getCurrentGame()

        val bonuses = gameStatistic.bonuses.map {
            if (it.type == bonusType) {
                if (it.count <= 0) throw NotEnoughBonusException("Not enough bonus ${bonusType}")
                it.copy(count = it.count - 1)
            } else it
        }
        val updatedGame = gameStatistic.copy(bonuses = bonuses).run {
            when (bonusType) {
                BonusType.ROTATE -> useBonusRotate()
                BonusType.REROLL -> useBonusReroll()
                else -> this
            }
        }

        repository.updateGameState(updatedGame)
    }
}

private fun GameStatisticItem.useBonusRotate(): GameStatisticItem {
    return this.copy(
        dice = this.dice.rotateDices()
    )
}

private fun GameStatisticItem.useBonusReroll(): GameStatisticItem {
    return this.copy(
        dice = DiceItem.empty()
    )
}
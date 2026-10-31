package com.example.dicedrop.domain.action

import com.example.dicedrop.domain.engine.AchievementTracker
import com.example.dicedrop.domain.entity.DiceItem
import com.example.dicedrop.domain.entity.statistic.PlayerStat
import com.example.dicedrop.domain.repository.GameRepository
import com.example.dicedrop.domain.useCases.game.RollDicesUseCase
import javax.inject.Inject

class RollDiceAction @Inject constructor(
    private val rollDicesUseCase: RollDicesUseCase,
    private val repository: GameRepository,
    private val achievementTracker: AchievementTracker
) {
    suspend operator fun invoke(result: DiceItem? = null): DiceItem {
        val tray = repository.getCurrentGame().tray
        val wasFrozen = tray.slotA?.frozen == true && tray.slotB?.frozen == true

        val dices = rollDicesUseCase(result)

        // Замороженные кубики не бросались заново: дубль из них не засчитываем
        if (wasFrozen) {
            achievementTracker.increment(PlayerStat.ROLL_COUNT)
        } else {
            achievementTracker.onDiceRolled(dices)
        }

        return dices
    }
}

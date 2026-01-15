package com.example.dicedrop.domain.action

import com.example.dicedrop.domain.engine.AchievementTracker
import com.example.dicedrop.domain.entity.DiceItem
import com.example.dicedrop.domain.entity.statistic.PlayerStat
import com.example.dicedrop.domain.repository.PlayerStatsRepository
import com.example.dicedrop.domain.useCases.game.RollDicesUseCase
import javax.inject.Inject

class RollDiceAction @Inject constructor(
    private val rollDicesUseCase: RollDicesUseCase,
    private val playerStatsRepository: PlayerStatsRepository,
    private val achievementTracker: AchievementTracker
) {
    suspend operator fun invoke(result: DiceItem? = null): DiceItem {
        val dices = rollDicesUseCase(result)

        playerStatsRepository.increment(PlayerStat.ROLL_COUNT)
        achievementTracker.onRoll()

        return dices
    }
}
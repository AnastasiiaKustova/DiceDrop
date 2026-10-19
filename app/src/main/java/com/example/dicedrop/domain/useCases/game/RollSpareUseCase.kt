package com.example.dicedrop.domain.useCases.game

import com.example.dicedrop.domain.engine.AchievementTracker
import com.example.dicedrop.domain.entity.TrayPhase
import com.example.dicedrop.domain.entity.statistic.PlayerStat
import com.example.dicedrop.domain.entity.withTray
import com.example.dicedrop.domain.repository.GameRepository
import javax.inject.Inject

class RollSpareUseCase @Inject constructor(
    private val repository: GameRepository,
    private val achievementTracker: AchievementTracker
) {
    suspend operator fun invoke(value: Int) {
        val game = repository.getCurrentGame()
        val spare = game.tray.spare ?: return
        if (spare.value != null) return
        val tray = game.tray.copy(
            spare = spare.copy(value = value),
            phase = TrayPhase.DISCARD
        )
        repository.updateGameState(game.withTray(tray))

        // Докинули к дублю и выпало то же число - бонус ничего не дал, за это утешительное достижение
        val a = game.tray.slotA?.value
        if (a != null && a == game.tray.slotB?.value && a == value) {
            achievementTracker.increment(PlayerStat.DRAFT_TRIPLE)
        }
    }
}

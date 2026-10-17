package com.example.dicedrop.domain.useCases.game

import com.example.dicedrop.core.NotEnoughBonusException
import com.example.dicedrop.domain.engine.AchievementTracker
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.domain.entity.DiceItem
import com.example.dicedrop.domain.entity.GameStatisticItem
import com.example.dicedrop.domain.entity.Slot
import com.example.dicedrop.domain.entity.TrayDie
import com.example.dicedrop.domain.entity.withTray
import com.example.dicedrop.domain.repository.GameRepository
import javax.inject.Inject

class UseBonusUseCase @Inject constructor(
    private val repository: GameRepository,
    private val achievementTracker: AchievementTracker
) {
    suspend operator fun invoke(bonusType: BonusType, slot: Slot? = null) {
        val gameStatistic = repository.getCurrentGame()

        if (gameStatistic.dice == DiceItem.empty()) return

        if (bonusType == BonusType.FREEZE && !canFreezeAll(gameStatistic)) return
        if (bonusType == BonusType.DRAFT && gameStatistic.tray.spare != null) return

        val bonuses = gameStatistic.bonuses.map {
            if (it.type == bonusType) {
                if (it.count <= 0) throw NotEnoughBonusException("Not enough bonus ${bonusType}")
                it.copy(count = it.count - 1)
            } else it
        }
        val updatedGame = gameStatistic.copy(bonuses = bonuses).run {
            when (bonusType) {
                BonusType.ROTATE -> useBonusRotate()
                BonusType.FREEZE -> freezeAll()
                BonusType.DRAFT -> addSpare()
                else -> this
            }
        }

        repository.updateGameState(updatedGame)
        achievementTracker.onBonusUsed(bonusType)
    }

    suspend fun setFace(value: Int, slot: Slot) {
        val game = repository.getCurrentGame()
        if (game.tray.spare != null) return
        val tray = game.tray
        val target = if (slot == Slot.A) tray.slotA else tray.slotB
        if (target?.value == null || target.value == 0) return
        val bonuses = game.bonuses.map {
            if (it.type == BonusType.LUCKY) {
                if (it.count <= 0) throw NotEnoughBonusException("Not enough bonus LUCKY")
                it.copy(count = it.count - 1)
            } else it
        }
        val newTray = if (slot == Slot.A) tray.copy(slotA = target.copy(value = value))
        else tray.copy(slotB = target.copy(value = value))
        repository.updateGameState(game.copy(bonuses = bonuses).withTray(newTray))
        achievementTracker.onBonusUsed(BonusType.LUCKY)
    }
}

private fun canFreezeAll(game: GameStatisticItem): Boolean {
    val a = game.tray.slotA
    val b = game.tray.slotB
    if (a?.value == null || a.value == 0) return false
    if (b?.value == null || b.value == 0) return false
    if (a.frozen && b.frozen) return false
    return true
}

private fun GameStatisticItem.freezeAll(): GameStatisticItem {
    val a = tray.slotA ?: return this
    val b = tray.slotB ?: return this
    return withTray(tray.copy(slotA = a.copy(frozen = true), slotB = b.copy(frozen = true)))
}

private fun GameStatisticItem.useBonusRotate(): GameStatisticItem {
    if (tray.spare != null) return this
    val newTray = tray.copy(slotA = tray.slotB, slotB = tray.slotA)
    val w = newTray.slotA?.value ?: 0
    val h = newTray.slotB?.value ?: 0
    return copy(tray = newTray, dice = dice.copy(dice1 = h, dice2 = w))
}

private fun GameStatisticItem.addSpare(): GameStatisticItem {
    if (tray.spare != null) return this
    return withTray(tray.copy(spare = TrayDie(value = null)))
}
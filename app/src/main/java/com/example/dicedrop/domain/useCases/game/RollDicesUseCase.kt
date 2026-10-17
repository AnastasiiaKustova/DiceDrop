package com.example.dicedrop.domain.useCases.game

import com.example.dicedrop.domain.entity.DiceItem
import com.example.dicedrop.domain.entity.TrayDie
import com.example.dicedrop.domain.entity.TrayPhase
import com.example.dicedrop.domain.entity.TrayState
import com.example.dicedrop.domain.entity.withTray
import com.example.dicedrop.domain.repository.GameRepository
import javax.inject.Inject

class RollDicesUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(result: DiceItem? = null): DiceItem {
        val currentGame = repository.getCurrentGame()
        if (currentGame.isGameOver) return currentGame.dice
        val rolled = result ?: DiceItem.randomDiceItem()
        val slotA = currentGame.tray.slotA
        val slotB = currentGame.tray.slotB
        val w = slotA?.takeIf { it.frozen && it.value != null }?.value ?: rolled.dice2
        val h = slotB?.takeIf { it.frozen && it.value != null }?.value ?: rolled.dice1
        val dices = DiceItem(dice1 = h, dice2 = w)
        val spare = currentGame.tray.spare?.let { sp ->
            if (sp.value != null) sp
            else TrayDie(DiceItem.randomDiceItem().dice1)
        }
        val tray = TrayState(
            slotA = TrayDie(w),
            slotB = TrayDie(h),
            spare = spare,
            phase = if (spare != null) TrayPhase.DISCARD else TrayPhase.SHOP
        )
        val updatedGame = currentGame.withTray(tray).copy(lastPlaced = emptyList())
        repository.updateGameState(updatedGame)
        return dices
    }
}
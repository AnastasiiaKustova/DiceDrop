package com.example.dicedrop.domain.useCases.game

import com.example.dicedrop.domain.entity.DiscardTarget
import com.example.dicedrop.domain.entity.TrayPhase
import com.example.dicedrop.domain.entity.withTray
import com.example.dicedrop.domain.repository.GameRepository
import javax.inject.Inject

class DiscardSpareUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(target: DiscardTarget) {
        val game = repository.getCurrentGame()
        val tray = game.tray
        val spare = tray.spare ?: return
        // Пока третий кубик катится, у него нет числа: блок собрать не из чего
        if (spare.value == null) return
        val newTray = when (target) {
            DiscardTarget.SPARE -> tray.copy(spare = null)
            DiscardTarget.A -> tray.copy(slotA = tray.slotB, slotB = spare, spare = null)
            DiscardTarget.B -> tray.copy(slotB = spare, spare = null)
        }.copy(phase = TrayPhase.ROLLED)
        repository.updateGameState(game.withTray(newTray))
    }
}
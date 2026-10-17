package com.example.dicedrop.domain.useCases.game

import com.example.dicedrop.domain.entity.TrayPhase
import com.example.dicedrop.domain.entity.withTray
import com.example.dicedrop.domain.repository.GameRepository
import javax.inject.Inject

class RollSpareUseCase @Inject constructor(
    private val repository: GameRepository
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
    }
}
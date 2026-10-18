package com.example.dicedrop.domain.useCases.game

import com.example.dicedrop.domain.repository.GameRepository
import javax.inject.Inject

// Награда за рекламу в конце игры: ещё столько же монет, сколько заработано за партию. Один раз за партию
class DoubleCoinsUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke() {
        val game = repository.getCurrentGame()
        if (game.coinsDoubled || game.coinsEarned <= 0) return

        repository.saveGame(
            game.copy(
                coins = game.coins + game.coinsEarned,
                coinsEarned = game.coinsEarned * 2,
                coinsDoubled = true
            )
        )
    }
}

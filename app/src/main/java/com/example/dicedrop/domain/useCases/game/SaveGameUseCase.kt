package com.example.dicedrop.domain.useCases.game

import com.example.dicedrop.domain.repository.GameRepository
import com.example.dicedrop.domain.entity.GameStatisticItem
import javax.inject.Inject

class SaveGameUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(game: GameStatisticItem) = repository.saveGame(game)
}
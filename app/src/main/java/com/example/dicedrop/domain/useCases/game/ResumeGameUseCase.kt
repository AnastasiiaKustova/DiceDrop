package com.example.dicedrop.domain.useCases.game

import com.example.dicedrop.domain.entity.GameStatisticItem
import com.example.dicedrop.domain.repository.GameRepository
import javax.inject.Inject

class ResumeGameUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(): GameStatisticItem = repository.getCurrentGame()
}
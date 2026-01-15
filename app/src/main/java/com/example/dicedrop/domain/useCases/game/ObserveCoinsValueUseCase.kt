package com.example.dicedrop.domain.useCases.game

import com.example.dicedrop.domain.repository.GameRepository
import javax.inject.Inject

class ObserveCoinsValueUseCase @Inject constructor(
    private val repository: GameRepository
) {
    operator fun invoke() = repository.coinsValueFlow
}
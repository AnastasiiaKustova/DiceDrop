package com.example.dicedrop.domain.useCases.game

import com.example.dicedrop.domain.entity.GameStatisticItem
import com.example.dicedrop.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveGameUseCase @Inject constructor(
    private val repository: GameRepository
) {
    operator fun invoke() = repository.gameStatisticFlow
}
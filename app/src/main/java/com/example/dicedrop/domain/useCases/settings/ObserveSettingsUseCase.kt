package com.example.dicedrop.domain.useCases.settings

import com.example.dicedrop.domain.entity.settings.GameSettings
import com.example.dicedrop.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke(): Flow<GameSettings> = repository.gameSettingsFlow
}

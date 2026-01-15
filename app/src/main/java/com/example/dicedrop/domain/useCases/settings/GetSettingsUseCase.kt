package com.example.dicedrop.domain.useCases.settings

import com.example.dicedrop.domain.entity.GameSettings
import com.example.dicedrop.domain.repository.SettingsRepository
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(): GameSettings = repository.getSettings()
}
package com.example.dicedrop.domain.useCases.settings

import com.example.dicedrop.domain.repository.SettingsRepository
import javax.inject.Inject

class ChangeUseAutoRollUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(useAutoRoll: Boolean) {
        val currentSettings = repository.getSettings()
        val newSettings = currentSettings.copy(
            useAutoRoll = useAutoRoll
        )
        repository.saveSettings(newSettings)
    }
}
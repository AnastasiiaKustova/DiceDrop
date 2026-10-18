package com.example.dicedrop.domain.useCases.settings

import com.example.dicedrop.domain.repository.SettingsRepository
import javax.inject.Inject

class ChangeMusicUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(isOn: Boolean) {
        val currentSettings = repository.getSettings()
        val newSettings = currentSettings.copy(
            isMusicOn = isOn
        )
        repository.saveSettings(newSettings)
    }
}

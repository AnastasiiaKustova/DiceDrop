package com.example.dicedrop.domain.useCases.settings

import com.example.dicedrop.domain.repository.SettingsRepository
import javax.inject.Inject

class MarkTutorialShownUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke() {
        val currentSettings = repository.getSettings()
        if (currentSettings.isTutorialShown) return
        repository.saveSettings(currentSettings.copy(isTutorialShown = true))
    }
}

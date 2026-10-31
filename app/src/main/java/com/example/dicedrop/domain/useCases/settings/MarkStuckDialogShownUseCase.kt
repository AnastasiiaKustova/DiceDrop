package com.example.dicedrop.domain.useCases.settings

import com.example.dicedrop.domain.repository.SettingsRepository
import javax.inject.Inject

class MarkStuckDialogShownUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke() {
        val currentSettings = repository.getSettings()
        repository.saveSettings(
            currentSettings.copy(stuckDialogsShown = currentSettings.stuckDialogsShown + 1)
        )
    }
}

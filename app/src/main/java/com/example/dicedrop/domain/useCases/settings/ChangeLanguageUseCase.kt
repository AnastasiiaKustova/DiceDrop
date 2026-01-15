package com.example.dicedrop.domain.useCases.settings

import com.example.dicedrop.domain.repository.SettingsRepository
import javax.inject.Inject

class ChangeLanguageUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(language: String) {
        val currentSettings = repository.getSettings()
        val newSettings = currentSettings.copy(
            language = language
        )
        repository.saveSettings(newSettings)
    }
}
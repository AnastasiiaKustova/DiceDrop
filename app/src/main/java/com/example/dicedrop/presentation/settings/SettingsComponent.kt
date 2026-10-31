package com.example.dicedrop.presentation.settings

import com.example.dicedrop.domain.entity.settings.SettingTypes
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface SettingsComponent {
    val model: StateFlow<SettingsStore.State>
    val uiEvents: SharedFlow<UiEvent>

    fun onBackClick()

    fun onExitClick()

    fun onNavigateToShopClick()

    fun onRestartGameClick()

    fun onSettingsChangeClick(settingType: SettingTypes)

    // Обучение пролистано до конца - для достижения
    fun onTutorialFinished()

    sealed interface UiEvent {
        data class ShowError(val error: ErrorType) : UiEvent

        enum class ErrorType {
            ChangeLanguageError
        }
    }
}
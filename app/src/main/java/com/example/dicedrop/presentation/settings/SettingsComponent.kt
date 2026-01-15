package com.example.dicedrop.presentation.settings

import com.example.dicedrop.domain.entity.SettingTypes
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

    fun onTurnOffAds()

    sealed interface UiEvent {
        data class ShowMessage(val message: MessageType) : UiEvent
        data class ShowError(val error: ErrorType) : UiEvent

        enum class MessageType {
            AdsOffSuccess
        }

        enum class ErrorType {
            ChangeLanguageError,
            AdsOffFailed
        }
    }
}
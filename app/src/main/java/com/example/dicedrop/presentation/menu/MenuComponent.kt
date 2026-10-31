package com.example.dicedrop.presentation.menu

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface MenuComponent {

    val model: StateFlow<MenuStore.State>

    val uiEvents: SharedFlow<UiEvent>

    fun onStartGameClick()

    fun onAchievementsClick()

    fun onShopClick()

    fun onSettingsClick()

    fun onGiftClick()

    fun onGiftConfirm()

    fun onTutorialClose()

    // Обучение пролистано до конца кнопкой «Играть!»
    fun onTutorialFinish()

    sealed interface UiEvent {
        data object GiftAlreadyReceived : UiEvent
    }

}

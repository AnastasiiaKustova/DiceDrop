package com.example.dicedrop.presentation.achievements

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface AchievementsComponent {

    val model: StateFlow<AchievementsStore.State>

    val uiEvents: SharedFlow<UiEvent>

    fun onBackClick()

    fun onGetRewardClick(achievementId: String)

    sealed interface UiEvent {
        data class AchievementRewardClaimed(val achievementId: String) : UiEvent
        data class Error(val msg: String) : UiEvent

    }
}
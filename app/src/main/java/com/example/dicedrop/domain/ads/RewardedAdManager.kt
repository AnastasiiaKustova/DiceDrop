package com.example.dicedrop.domain.ads

import kotlinx.coroutines.flow.StateFlow

interface RewardedAdManager {
    val state: StateFlow<RewardedAdState>
    fun showAd(onReward: () -> Unit)

    sealed interface RewardedAdState {
        object Idle : RewardedAdState
        object Loading : RewardedAdState
        object Showing : RewardedAdState
        object Rewarded : RewardedAdState
    }
}


package com.example.dicedrop.domain.ads

import android.app.Activity
import com.example.dicedrop.domain.entity.AdReward
import kotlinx.coroutines.flow.StateFlow

interface RewardedAdManager {
    val state: StateFlow<RewardedAdState>

    // onReward - только если ролик досмотрен; onUnavailable - если рекламу не удалось загрузить или показать
    fun showAd(
        activity: Activity,
        reward: AdReward,
        onReward: () -> Unit,
        onUnavailable: () -> Unit
    )

    sealed interface RewardedAdState {
        object Idle : RewardedAdState
        object Loading : RewardedAdState
        object Showing : RewardedAdState
        object Rewarded : RewardedAdState
    }
}

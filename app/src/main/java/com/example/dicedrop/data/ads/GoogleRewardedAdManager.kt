package com.example.dicedrop.data.ads

import android.app.Activity
import android.content.Context
import com.example.dicedrop.domain.ads.RewardedAdManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GoogleRewardedAdManager(
    private val context: Context
) : RewardedAdManager {

    private val _state = MutableStateFlow<RewardedAdManager.RewardedAdState>(RewardedAdManager.RewardedAdState.Idle)
    override val state: StateFlow<RewardedAdManager.RewardedAdState> = _state

    override fun showAd(onReward: () -> Unit) {
        // Google Mobile Ads SDK
        /*rewardedAd.show(
            context as Activity
        ) {
            onReward()
        }*/
    }
}
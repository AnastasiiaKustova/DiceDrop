package com.example.dicedrop.data.ads

import com.example.dicedrop.domain.ads.RewardedAdManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FakeRewardedAdManager : RewardedAdManager {

    private val _state = MutableStateFlow<RewardedAdManager.RewardedAdState>(RewardedAdManager.RewardedAdState.Idle)
    override val state: StateFlow<RewardedAdManager.RewardedAdState> = _state
    override fun showAd(onReward: () -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            _state.value = RewardedAdManager.RewardedAdState.Loading
            delay(800)

            _state.value = RewardedAdManager.RewardedAdState.Showing
            delay(1_200)

            _state.value = RewardedAdManager.RewardedAdState.Rewarded
            delay(300)
            onReward()

            _state.value = RewardedAdManager.RewardedAdState.Idle
        }
    }
}
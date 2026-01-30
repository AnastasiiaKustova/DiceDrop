package com.example.dicedrop.presentation.game

import android.content.Context
import com.example.dicedrop.R
import com.example.dicedrop.domain.ads.RewardedAdManager
import com.example.dicedrop.domain.entity.BonusItem
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.domain.entity.DiceItem
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface GameComponent {

    val model: StateFlow<GameStore.State>

    val rewardedAdManager: RewardedAdManager

    val uiEvents: SharedFlow<UiEvent>

    fun onAchievementClick()

    fun onShopClick()

    fun onRollEnd(result: DiceItem)

    fun onBonusClick(bonusType: BonusType)

    fun onPlaceBonus(r: Int, c: Int, bonusType: BonusType)

    fun onBuyBonusClick(bonusType: BonusType)

    fun onWatchAdsClick()

    fun AdRewardReceive()

    fun placeBlock(r: Int, c: Int)

    fun restartGame()

    fun onBackPressed()

    fun onConfirmNewRecord(rewardsList: List<BonusItem>)

    sealed interface UiEvent {
        data object WatchAd : UiEvent
        data object Animate : UiEvent
        data class Error(val msg: ErrorType) : UiEvent
        data class ClearLine(val clearedRows: List<Int>, val clearedCols: List<Int>) : UiEvent

        enum class ErrorType(val stringRes: Int) {
            NotEnoughCoins(R.string.not_enough_coins),
            NotEnoughBonus(R.string.not_enough_bonus)
        }
    }
}
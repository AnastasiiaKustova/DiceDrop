package com.example.dicedrop.presentation.game

import com.example.dicedrop.R
import com.example.dicedrop.domain.ads.RewardedAdManager
import com.example.dicedrop.domain.entity.AdReward
import com.example.dicedrop.domain.entity.BonusItem
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.domain.entity.DiceItem
import com.example.dicedrop.domain.entity.DiscardTarget
import com.example.dicedrop.domain.entity.Slot
import com.example.dicedrop.domain.entity.settings.SettingTypes
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

    fun onBuyBonusClick(bonusType: BonusType)

    fun onWatchAdsClick()

    fun onDoubleCoinsClick()

    fun AdRewardReceive(reward: AdReward)

    fun placeBlock(r: Int, c: Int)

    fun restartGame()

    fun onBackPressed()

    fun onConfirmNewRecord(rewardsList: List<BonusItem>)

    fun onDiscardClick(target: DiscardTarget)

    fun onFreezeAll()

    fun onSpareRolled(value: Int)

    fun onLuckyFace(value: Int, slot: Slot)

    fun onSettingsChange(setting: SettingTypes)

    fun onStuckDialogClosed()

    sealed interface UiEvent {
        data class WatchAd(val reward: AdReward) : UiEvent
        data object Animate : UiEvent
        data object BonusBought : UiEvent
        // bonusType - какой бонус не удалось купить, чтобы показать его название и цену
        data class Error(val msg: ErrorType, val bonusType: BonusType? = null) : UiEvent
        data class ClearLine(
            val clearedRows: List<Int>,
            val clearedCols: List<Int>,
            val combo: Int,
            val points: Int,
            val coins: Int
        ) : UiEvent
        data class AchievementUnlocked(
            val titleId: Int,
            val descriptionId: Int,
            val reward: Int
        ) : UiEvent

        enum class ErrorType(val stringRes: Int) {
            NotEnoughCoins(R.string.not_enough_coins),
            NotEnoughBonus(R.string.not_enough_bonus)
        }
    }
}
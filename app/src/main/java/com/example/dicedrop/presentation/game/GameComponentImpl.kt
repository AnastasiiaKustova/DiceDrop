package com.example.dicedrop.presentation.game

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnResume
import com.arkivanov.essenty.lifecycle.doOnStop
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.dicedrop.core.componentScope
import com.example.dicedrop.domain.ads.RewardedAdManager
import com.example.dicedrop.domain.entity.AdReward
import com.example.dicedrop.domain.entity.BonusItem
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.domain.entity.DiceItem
import com.example.dicedrop.domain.entity.DiscardTarget
import com.example.dicedrop.domain.entity.Slot
import com.example.dicedrop.domain.entity.settings.SettingTypes
import com.example.dicedrop.presentation.game.GameComponent.UiEvent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GameComponentImpl @AssistedInject constructor(
    private val storeFactory: GameStoreFactory,
    private val rewardedAdManagerImpl: RewardedAdManager,
    @Assisted("navigateBackClicked") private val navigateBackClicked:() -> Unit,
    @Assisted("navigateToAchievementsClicked") private val navigateToAchievementsClicked:() -> Unit,

    @Assisted("navigateToShopClicked") private val navigateToShopClicked:() -> Unit,
    @Assisted("componentContext") componentContext: ComponentContext,
) : GameComponent, ComponentContext by componentContext {

    private val _uiEvents = MutableSharedFlow<UiEvent>(extraBufferCapacity = 8)
    override val uiEvents: SharedFlow<UiEvent> = _uiEvents

    private val store: GameStore = instanceKeeper.getStore {
        storeFactory.create()
    }

    override val rewardedAdManager: RewardedAdManager = rewardedAdManagerImpl

    private val scope = componentScope()

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<GameStore.State> = store.stateFlow

    init {
        scope.launch {
            store.labels.collect{
                when (it){
                    is GameStore.Label.AnimateDice -> _uiEvents.emit(UiEvent.Animate)
                    GameStore.Label.ClickNavigateBack -> navigateBackClicked()
                    GameStore.Label.ClickNavigateToAchievements -> navigateToAchievementsClicked()
                    GameStore.Label.ClickNavigateToShop -> navigateToShopClicked()
                    is GameStore.Label.AchievementUnlocked -> _uiEvents.emit(
                        UiEvent.AchievementUnlocked(it.titleId, it.descriptionId, it.reward)
                    )
                    is GameStore.Label.WatchAd -> _uiEvents.emit(UiEvent.WatchAd(it.reward))
                    GameStore.Label.BonusBought -> _uiEvents.emit(UiEvent.BonusBought)
                    is GameStore.Label.NotEnoughCoins -> _uiEvents.emit(
                        UiEvent.Error(GameComponent.UiEvent.ErrorType.NotEnoughCoins, it.bonusType)
                    )
                    GameStore.Label.NotEnoughBonus -> _uiEvents.emit(UiEvent.Error(GameComponent.UiEvent.ErrorType.NotEnoughBonus))
                    is GameStore.Label.PlayLineClearAnimation -> _uiEvents.emit(UiEvent.ClearLine(
                        clearedRows = it.clearedRows,
                        clearedCols = it.clearedCols,
                        combo = it.combo,
                        points = it.points,
                        coins = it.coins))
                }
            }
        }

        lifecycle.doOnResume {
            scope.launch {
                store.accept(GameStore.Intent.ResumeGame)
            }
        }

        lifecycle.doOnStop {
            scope.launch {
                store.accept(GameStore.Intent.SaveGame)
            }
        }
    }

    override fun onBackPressed() {
        store.accept(GameStore.Intent.BackPressed)
    }

    override fun onFreezeAll() {
        store.accept(GameStore.Intent.FreezeAll)
    }

    override fun onDiscardClick(target: DiscardTarget) {
        store.accept(GameStore.Intent.DiscardDie(target))
    }

    override fun onSpareRolled(value: Int) {
        store.accept(GameStore.Intent.SpareRolled(value))
    }

    override fun onLuckyFace(value: Int, slot: Slot) {
        store.accept(GameStore.Intent.LuckyFace(value, slot))
    }

    override fun onSettingsChange(setting: SettingTypes) {
        store.accept(GameStore.Intent.ChangeSetting(setting))
    }

    override fun onConfirmNewRecord(rewardsList: List<BonusItem>) {
        store.accept(GameStore.Intent.NewRecordConfirm(rewardsList))
    }

    override fun onAchievementClick() {
        store.accept(GameStore.Intent.ClickNavigateToAchievements)
    }

    override fun onShopClick() {
        store.accept(GameStore.Intent.ClickNavigateToShop)
    }
    override fun onRollEnd(result: DiceItem) {
        store.accept(GameStore.Intent.RollDice(result))
    }

    override fun onBonusClick(bonusType: BonusType) {
        store.accept(GameStore.Intent.UseBonus(bonusType))
    }

    override fun onBuyBonusClick(bonusType: BonusType) {
        store.accept(GameStore.Intent.BuyBonus(bonusType))
    }

    override fun onWatchAdsClick() {
        store.accept(GameStore.Intent.WatchAd(AdReward.REVIVE))
    }

    override fun onDoubleCoinsClick() {
        store.accept(GameStore.Intent.WatchAd(AdReward.DOUBLE_COINS))
    }

    override fun AdRewardReceive(reward: AdReward) {
        store.accept(GameStore.Intent.AdRewardReceive(reward))
    }

    override fun placeBlock(r: Int, c: Int) {
        store.accept(GameStore.Intent.PlaceBlock(r, c))
    }

    override fun restartGame() {
        store.accept(GameStore.Intent.RestartGame)
    }

    @AssistedFactory
    interface Factory{
        fun create(
            @Assisted("navigateBackClicked") navigateBackClicked:() -> Unit,
            @Assisted("navigateToAchievementsClicked") navigateToAchievementsClicked:() -> Unit,
            @Assisted("navigateToShopClicked") navigateToShopClicked:() -> Unit,
            @Assisted("componentContext") componentContext: ComponentContext,
        ): GameComponentImpl
    }
}
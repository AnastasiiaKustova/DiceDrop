package com.example.dicedrop.presentation.game

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.dicedrop.core.NotEnoughBonusException
import com.example.dicedrop.core.NotEnoughCoinsException
import com.example.dicedrop.domain.action.CheckAndClearFullRowsAction
import com.example.dicedrop.domain.action.RollDiceAction
import com.example.dicedrop.domain.catalog.ShopItemCatalog
import com.example.dicedrop.domain.entity.achievement.Achievement
import com.example.dicedrop.domain.engine.AchievementTracker
import com.example.dicedrop.domain.entity.AdReward
import com.example.dicedrop.domain.entity.BonusItem
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.domain.entity.DiceItem
import com.example.dicedrop.domain.entity.DiscardTarget
import com.example.dicedrop.domain.entity.GameStatisticItem
import com.example.dicedrop.domain.entity.Slot
import com.example.dicedrop.domain.entity.settings.GameSettings
import com.example.dicedrop.domain.entity.settings.SettingTypes
import com.example.dicedrop.domain.useCases.achievements.GetAchievementsUseCase
import com.example.dicedrop.domain.useCases.achievements.ObserveAchievementStateUseCase
import com.example.dicedrop.domain.useCases.game.CheckGameOverUseCase
import com.example.dicedrop.domain.useCases.game.ConfirmNewRecordUseCase
import com.example.dicedrop.domain.useCases.game.DiscardSpareUseCase
import com.example.dicedrop.domain.useCases.game.DoubleCoinsUseCase
import com.example.dicedrop.domain.useCases.game.GetRecordRewardsUseCase
import com.example.dicedrop.domain.useCases.game.GrantAdRewardUseCase
import com.example.dicedrop.domain.useCases.game.LoadGameUseCase
import com.example.dicedrop.domain.useCases.game.ObserveGameUseCase
import com.example.dicedrop.domain.useCases.game.PlaceBlockUseCase
import com.example.dicedrop.domain.useCases.game.RestartGameUseCase
import com.example.dicedrop.domain.useCases.game.ResumeGameUseCase
import com.example.dicedrop.domain.useCases.game.RollSpareUseCase
import com.example.dicedrop.domain.useCases.game.SaveGameUseCase
import com.example.dicedrop.domain.useCases.game.UseBonusUseCase
import com.example.dicedrop.domain.useCases.settings.ChangeMusicUseCase
import com.example.dicedrop.domain.useCases.settings.ChangeSoundUseCase
import com.example.dicedrop.domain.useCases.settings.ChangeVibrationUseCase
import com.example.dicedrop.domain.useCases.settings.MarkStuckDialogShownUseCase
import com.example.dicedrop.domain.useCases.settings.ObserveSettingsUseCase
import com.example.dicedrop.domain.useCases.shop.BuyBonusUseCase
import com.example.dicedrop.presentation.game.GameStore.Intent
import com.example.dicedrop.presentation.game.GameStore.Label
import com.example.dicedrop.presentation.game.GameStore.State
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

interface GameStore : Store<Intent, State, Label> {

    //Если пользователь кликает по экрану, то он отправляет Intent
    sealed interface Intent {

        data object ClickNavigateToShop : Intent

        data object ClickNavigateToAchievements : Intent

        data object BackPressed : Intent

        data class RollDice(val result: DiceItem) : Intent

        data class PlaceBlock(val r: Int, val c: Int) : Intent

        data class UseBonus(val bonusType: BonusType) : Intent

        data class BuyBonus(val bonusType: BonusType) : Intent

        data class WatchAd(val reward: AdReward) : Intent

        data class AdRewardReceive(val reward: AdReward) : Intent

        data object RestartGame : Intent

        data object ResumeGame : Intent

        data object SaveGame : Intent

        data class NewRecordConfirm(val rewardsList: List<BonusItem>) : Intent

        data object FreezeAll : Intent

        data class DiscardDie(val target: DiscardTarget) : Intent

        data class SpareRolled(val value: Int) : Intent

        data class LuckyFace(val value: Int, val slot: Slot) : Intent

        // Переключатели звуков, музыки и вибрации в окне паузы
        data class ChangeSetting(val setting: SettingTypes) : Intent

        // Игрок закрыл окно «Ходов нет»
        data object StuckDialogClosed : Intent
    }

    data class State(
        val gameState: GameState,
        // Сколько выполненных достижений ждут, пока игрок заберёт награду
        val rewardsToClaim: Int = 0,
        val settings: GameSettings = GameSettings.default()
    ) {
        sealed interface GameState {

            data object Initial : GameState

            data object Loading : GameState

            data object Error : GameState

            data class Over(
                val score: Int,
                val prevState: Loaded
            ) : GameState

            data class NewRecord(
                val score: Int,
                val rewardsList: List<BonusItem>,
                val prevState: Loaded
            ) : GameState

            data class Loaded(
                val gameStatistic: GameStatisticItem
            ) : GameState
        }
    }

    //Действия, которые делает пользователь (только?)
    sealed interface Label {

        data object ClickNavigateBack : Label

        data object ClickNavigateToShop : Label

        data object ClickNavigateToAchievements : Label

        data class AchievementUnlocked(
            val titleId: Int,
            val descriptionId: Int,
            val reward: Int
        ) : Label

        data class NotEnoughCoins(val bonusType: BonusType) : Label

        data object NotEnoughBonus : Label

        data object BonusBought : Label

        data class AnimateDice(val dices: DiceItem) : Label

        data class PlayLineClearAnimation(
            val clearedRows: List<Int>,
            val clearedCols: List<Int>,
            val combo: Int,
            val points: Int,
            val coins: Int
        ) : Label

        data class WatchAd(val reward: AdReward) : Label
    }
}

class GameStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val saveGameUseCase: SaveGameUseCase,
    private val loadGameUseCase: LoadGameUseCase,
    private val placeBlockUseCase: PlaceBlockUseCase,
    private val checkGameOverUseCase: CheckGameOverUseCase,
    private val rollDiceAction: RollDiceAction,
    private val checkAndClearFullRowsAction: CheckAndClearFullRowsAction,
    private val useBonusUseCase: UseBonusUseCase,
    private val buyBonusUseCase: BuyBonusUseCase,
    private val getRecordRewardsUseCase: GetRecordRewardsUseCase,
    private val confirmNewRecordUseCase: ConfirmNewRecordUseCase,
    private val restartGameUseCase: RestartGameUseCase,
    private val resumeGameUseCase: ResumeGameUseCase,
    private val grantAdRewardUseCase: GrantAdRewardUseCase,
    private val doubleCoinsUseCase: DoubleCoinsUseCase,
    private val observeGameUseCase: ObserveGameUseCase,
    private val discardSpareUseCase: DiscardSpareUseCase,
    private val rollSpareUseCase: RollSpareUseCase,
    private val achievementTracker: AchievementTracker,
    private val getAchievementsUseCase: GetAchievementsUseCase,
    private val observeAchievementStateUseCase: ObserveAchievementStateUseCase,
    private val observeSettingsUseCase: ObserveSettingsUseCase,
    private val changeSoundUseCase: ChangeSoundUseCase,
    private val changeMusicUseCase: ChangeMusicUseCase,
    private val changeVibrationUseCase: ChangeVibrationUseCase,
    private val markStuckDialogShownUseCase: MarkStuckDialogShownUseCase
) {

    fun create(): GameStore =
        object : GameStore,
            Store<Intent, State, Label> by storeFactory.create(
                name = "GameStore",
                initialState = State(
                    gameState = State.GameState.Initial
                ),
                bootstrapper = BootstrapperImpl(),
                reducer = ReducerImpl,
                executorFactory = ::ExecutorImpl
            ) {}

    //Стейт экрана может меняться от данных в репозитории, если данные изменились и стейт надо перерисовать, то репозиторий отправляет Action
    private sealed interface Action {

        data class GameLoaded(val game: GameStatisticItem) : Action

        data object GameLoading : Action

        data object GameError : Action

        data class AchievementUnlocked(val achievement: Achievement) : Action

        data class RewardsChanged(val count: Int) : Action

        data class SettingsChanged(val settings: GameSettings) : Action
    }

    private sealed interface Msg {

        data class GameLoaded(val game: GameStatisticItem) : Msg

        data class RewardsChanged(val count: Int) : Msg

        data class SettingsChanged(val settings: GameSettings) : Msg

        data object GameLoading : Msg

        data object GameError : Msg

        data class GameOver(val game: GameStatisticItem) : Msg

        data class NewRecord(val game: GameStatisticItem, val rewardsList: List<BonusItem>) : Msg
    }

    //Если какие-то действия необходимо выполнить при создании стора (например подписаться на флоу из репозитория), то это нужно делать в Bootstrapper
    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                dispatch(Action.GameLoading)
                try {
                    loadGameUseCase()
                } catch (e: Exception) {
                    dispatch(Action.GameError)
                }
            }

            scope.launch {
                observeGameUseCase()
                    .distinctUntilChanged()
                    .collect { game ->
                        // После конца игры бонусы могли прийти из достижений или магазина -
                        // тогда ход снова может найтись, перепроверяем
                        val actualGame = if (game.isGameOver) checkGameOverUseCase() else game
                        dispatch(Action.GameLoaded(actualGame))
                    }
            }

            scope.launch {
                achievementTracker.events.collect { event ->
                    dispatch(Action.AchievementUnlocked(event.achievement))
                }
            }

            scope.launch {
                observeAchievementStateUseCase().collect {
                    dispatch(Action.RewardsChanged(countRewardsToClaim()))
                }
            }

            scope.launch {
                observeSettingsUseCase().collect {
                    dispatch(Action.SettingsChanged(it))
                }
            }
        }
    }

    private suspend fun countRewardsToClaim(): Int = try {
        getAchievementsUseCase().count { !it.isRewardClaimed && it.totalCount >= it.requiredCount }
    } catch (e: Exception) {
        0
    }

    //Обрабатывает Action и Intent, если нужно изменить стейт экрана, то отправляем Msg вызывая dispatch, этот Msg поступает в Reducer
    //Если стейт экрана менять не нужно, то отправяем Label вызывая publish
    private inner class ExecutorImpl :
        CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        private var placing = false

        override fun executeAction(action: Action, getState: () -> State) {
            when (action) {
                Action.GameError -> dispatch(Msg.GameError)
                is Action.GameLoaded -> showGame(action.game)
                Action.GameLoading -> dispatch(Msg.GameLoading)
                is Action.AchievementUnlocked -> {
                    publish(
                        Label.AchievementUnlocked(
                            titleId = action.achievement.titleId,
                            descriptionId = action.achievement.descriptionId,
                            reward = action.achievement.reward
                        )
                    )
                    scope.launch { dispatch(Msg.RewardsChanged(countRewardsToClaim())) }
                }
                is Action.RewardsChanged -> dispatch(Msg.RewardsChanged(action.count))
                is Action.SettingsChanged -> dispatch(Msg.SettingsChanged(action.settings))
            }
        }

        // Единая точка выбора экрана по данным игры: сначала рекорд, потом конец игры
        private fun showGame(game: GameStatisticItem) {
            when {
                game.isNewRecord -> dispatch(
                    Msg.NewRecord(
                        game = game,
                        // bestScore ещё прежний: он обновится, когда игрок заберёт награду
                        rewardsList = getRecordRewardsUseCase(previousBest = game.bestScore)
                    )
                )

                game.isGameOver -> dispatch(Msg.GameOver(game))

                else -> dispatch(Msg.GameLoaded(game))
            }
        }

        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                Intent.ClickNavigateToAchievements -> {
                    scope.launch {
                        saveIfLoaded(getState)
                        publish(Label.ClickNavigateToAchievements)
                    }
                }

                Intent.ClickNavigateToShop -> {
                    scope.launch {
                        saveIfLoaded(getState)
                        publish(Label.ClickNavigateToShop)
                    }
                }
                is Intent.PlaceBlock -> {
                    if (placing) return
                    placing = true
                    scope.launch {
                        try {
                            val placed = placeBlockUseCase(intent.r, intent.c) ?: return@launch
                            val clearedResult = checkAndClearFullRowsAction(placed)

                            if (clearedResult.clearedLinesCount > 0) {
                                publish(
                                    Label.PlayLineClearAnimation(
                                        clearedRows = clearedResult.clearedRows,
                                        clearedCols = clearedResult.clearedCols,
                                        combo = clearedResult.combo,
                                        points = clearedResult.points,
                                        coins = clearedResult.coins
                                    )
                                )
                            }
                        } finally {
                            placing = false
                        }
                    }
                }

                is Intent.RollDice -> {
                    scope.launch {
                        rollDiceAction(intent.result)
                        checkGameOverUseCase()
                    }
                }

                is Intent.UseBonus -> {
                    scope.launch {
                        try {
                            useBonusUseCase(intent.bonusType)
                            if (intent.bonusType != BonusType.DRAFT) checkGameOverUseCase()
                        } catch (e: NotEnoughBonusException) {
                            publish(Label.NotEnoughBonus)
                        }
                    }
                }

                is Intent.SpareRolled -> {
                    scope.launch { rollSpareUseCase(intent.value) }
                }

                is Intent.FreezeAll -> {
                    scope.launch {
                        try { useBonusUseCase(BonusType.FREEZE) }
                        catch (e: NotEnoughBonusException) { publish(Label.NotEnoughBonus) }
                    }
                }

                is Intent.DiscardDie -> {
                    scope.launch { discardSpareUseCase(intent.target); checkGameOverUseCase() }
                }

                is Intent.LuckyFace -> {
                    scope.launch {
                        try { useBonusUseCase.setFace(intent.value, intent.slot); checkGameOverUseCase() }
                        catch (e: NotEnoughBonusException) { publish(Label.NotEnoughBonus) }
                    }
                }

                is Intent.NewRecordConfirm -> {
                    scope.launch {
                        confirmNewRecordUseCase(intent.rewardsList)
                    }
                }

                Intent.RestartGame -> {
                    scope.launch {
                        restartGameUseCase()
                        showGame(checkGameOverUseCase())
                    }
                }

                is Intent.WatchAd -> {
                    publish(Label.WatchAd(intent.reward))
                }

                is Intent.BuyBonus -> {
                    scope.launch {
                        try {
                            buyBonusUseCase(
                                bonusType = intent.bonusType,
                                price = ShopItemCatalog.all
                                    .firstOrNull { it.bonusType == intent.bonusType }?.price ?: 1
                            )
                            publish(Label.BonusBought)
                        } catch (e: NotEnoughCoinsException) {
                            publish(Label.NotEnoughCoins(intent.bonusType))
                        }
                    }
                }

                Intent.ResumeGame -> {
                    scope.launch {
                        showGame(resumeGameUseCase())
                    }
                    // Пока игрок был в достижениях, он мог забрать награды
                    scope.launch {
                        dispatch(Msg.RewardsChanged(countRewardsToClaim()))
                    }
                }

                Intent.SaveGame -> {
                    scope.launch {
                        saveIfLoaded(getState)
                    }
                }

                Intent.BackPressed -> {
                    scope.launch {
                        saveIfLoaded(getState)
                        publish(Label.ClickNavigateBack)
                    }
                }

                is Intent.ChangeSetting -> {
                    // Новые настройки придут обратно через observeSettingsUseCase
                    scope.launch {
                        when (val setting = intent.setting) {
                            is SettingTypes.Sound -> changeSoundUseCase(setting.isOn)
                            is SettingTypes.Music -> changeMusicUseCase(setting.isOn)
                            is SettingTypes.Vibration -> changeVibrationUseCase(setting.isOn)
                            is SettingTypes.Language -> Unit
                        }
                    }
                }

                Intent.StuckDialogClosed -> {
                    scope.launch { markStuckDialogShownUseCase() }
                }

                is Intent.AdRewardReceive -> {
                    scope.launch {
                        when (intent.reward) {
                            AdReward.REVIVE -> grantAdRewardUseCase()
                            // Удвоение завершает партию - сразу начинаем новую
                            AdReward.DOUBLE_COINS -> {
                                doubleCoinsUseCase()
                                restartGameUseCase()
                                showGame(checkGameOverUseCase())
                            }
                            // Выдаётся в магазине
                            AdReward.RANDOM_BONUS -> Unit
                        }
                    }
                }
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State {
            return when (msg) {
                Msg.GameError -> copy(gameState = State.GameState.Error)
                Msg.GameLoading -> copy(gameState = State.GameState.Loading)
                // Состояние строится из самой игры, поэтому не зависит от текущего экрана
                is Msg.GameOver -> {
                    copy(
                        gameState = State.GameState.Over(
                            score = msg.game.totalScore,
                            prevState = State.GameState.Loaded(msg.game)
                        )
                    )
                }

                is Msg.NewRecord -> {
                    // Если окно рекорда уже открыто, награды не перегенерируем
                    val rewardsList = (gameState as? State.GameState.NewRecord)?.rewardsList
                        ?: msg.rewardsList

                    copy(
                        gameState = State.GameState.NewRecord(
                            score = msg.game.totalScore,
                            rewardsList = rewardsList,
                            prevState = State.GameState.Loaded(msg.game)
                        )
                    )
                }

                is Msg.GameLoaded -> copy(gameState = State.GameState.Loaded(msg.game))
                is Msg.RewardsChanged -> copy(rewardsToClaim = msg.count)
                is Msg.SettingsChanged -> copy(settings = msg.settings)
            }
        }
    }

    private suspend fun saveIfLoaded(
        getState: () -> State
    ) {
        val state = getState()
        val game = (state.gameState as? State.GameState.Loaded)?.gameStatistic ?: return
        saveGameUseCase(game)
    }
}
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
import com.example.dicedrop.domain.engine.AchievementTracker
import com.example.dicedrop.domain.entity.BonusItem
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.domain.entity.DiceItem
import com.example.dicedrop.domain.entity.DiscardTarget
import com.example.dicedrop.domain.entity.GameStatisticItem
import com.example.dicedrop.domain.entity.Slot
import com.example.dicedrop.domain.useCases.game.CheckGameOverUseCase
import com.example.dicedrop.domain.useCases.game.ConfirmNewRecordUseCase
import com.example.dicedrop.domain.useCases.game.DiscardSpareUseCase
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

        data object WatchAd : Intent

        data object AdRewardReceive : Intent

        data object RestartGame : Intent

        data object ResumeGame : Intent

        data object SaveGame : Intent

        data class NewRecordConfirm(val rewardsList: List<BonusItem>) : Intent

        data object FreezeAll : Intent

        data class DiscardDie(val target: DiscardTarget) : Intent

        data class SpareRolled(val value: Int) : Intent

        data class LuckyFace(val value: Int, val slot: Slot) : Intent
    }

    data class State(
        val gameState: GameState
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

        data class AchievementUnlocked(val titleId: Int) : Label

        data object NotEnoughCoins : Label

        data object NotEnoughBonus : Label

        data class AnimateDice(val dices: DiceItem) : Label

        data class PlayLineClearAnimation(
            val clearedRows: List<Int>,
            val clearedCols: List<Int>,
            val combo: Int,
            val points: Int
        ) : Label

        data object WatchAd : Label
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
    private val observeGameUseCase: ObserveGameUseCase,
    private val discardSpareUseCase: DiscardSpareUseCase,
    private val rollSpareUseCase: RollSpareUseCase,
    private val achievementTracker: AchievementTracker
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

        data class AchievementUnlocked(val titleId: Int) : Action
    }

    private sealed interface Msg {

        data class GameLoaded(val game: GameStatisticItem) : Msg

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
                        dispatch(Action.GameLoaded(game))
                    }
            }

            scope.launch {
                achievementTracker.events.collect { event ->
                    dispatch(Action.AchievementUnlocked(event.achievement.titleId))
                }
            }
        }
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
                is Action.AchievementUnlocked -> publish(Label.AchievementUnlocked(action.titleId))
            }
        }

        // Единая точка выбора экрана по данным игры: сначала рекорд, потом конец игры
        private fun showGame(game: GameStatisticItem) {
            when {
                game.isNewRecord -> dispatch(
                    Msg.NewRecord(
                        game = game,
                        rewardsList = getRecordRewardsUseCase(newRecord = game.totalScore)
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
                                        points = clearedResult.points
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

                Intent.WatchAd -> {
                    publish(Label.WatchAd)
                }

                is Intent.BuyBonus -> {
                    scope.launch {
                        try {
                            buyBonusUseCase(intent.bonusType)
                        } catch (e: NotEnoughCoinsException) {
                            publish(Label.NotEnoughCoins)
                        }
                    }
                }

                Intent.ResumeGame -> {
                    scope.launch {
                        showGame(resumeGameUseCase())
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

                Intent.AdRewardReceive -> {
                    scope.launch {
                        grantAdRewardUseCase()
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
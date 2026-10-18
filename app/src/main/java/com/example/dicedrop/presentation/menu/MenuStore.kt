package com.example.dicedrop.presentation.menu

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.dicedrop.domain.entity.BonusItem
import com.example.dicedrop.domain.entity.DailyReward
import com.example.dicedrop.domain.useCases.shop.GetDailyRewardIsReceivedStateUseCase
import com.example.dicedrop.domain.useCases.shop.GetDailyRewardUseCase
import com.example.dicedrop.domain.useCases.achievements.GetAchievementsUseCase
import com.example.dicedrop.domain.useCases.achievements.ObserveAchievementStateUseCase
import com.example.dicedrop.domain.useCases.game.ObserveGameUseCase
import com.example.dicedrop.domain.useCases.shop.ObserveDailyRewardUseCase
import com.example.dicedrop.domain.useCases.settings.GetSettingsUseCase
import com.example.dicedrop.domain.useCases.settings.MarkTutorialShownUseCase
import com.example.dicedrop.presentation.menu.MenuStore.Intent
import com.example.dicedrop.presentation.menu.MenuStore.Label
import com.example.dicedrop.presentation.menu.MenuStore.State
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

interface MenuStore : Store<Intent, State, Label> {

    sealed interface Intent {

        data object ClickNavigateToGame : Intent

        data object ClickNavigateToAchievements : Intent

        data object ClickNavigateToShop : Intent

        data object ClickNavigateToSettings : Intent

        data object Refresh : Intent

        data object ClickGift : Intent

        data object GiftConfirm : Intent

        data object TutorialClose : Intent

    }

    data class State(
        val coins: Int = 0,
        val bestScore: Int = 0,
        val rewardsToClaim: Int = 0,
        val giftReady: Boolean = false,
        val streakDay: Int = 1,
        // Полученный подарок, пока открыто окно "Вы получили"
        // Что выдал подарок: монеты и, в 7-й день серии, бонус
        val receivedGift: List<BonusItem>? = null,
        // Обучение при первом запуске
        val showTutorial: Boolean = false
    )

    sealed interface Label {

        data object ClickNavigateToGame : Label

        data object ClickNavigateToAchievements : Label

        data object ClickNavigateToShop : Label

        data object ClickNavigateToSettings : Label

        data object GiftAlreadyReceived : Label
    }
}

class MenuStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val observeGameUseCase: ObserveGameUseCase,
    private val observeDailyRewardUseCase: ObserveDailyRewardUseCase,
    private val observeAchievementStateUseCase: ObserveAchievementStateUseCase,
    private val getAchievementsUseCase: GetAchievementsUseCase,
    private val getDailyRewardIsReceivedStateUseCase: GetDailyRewardIsReceivedStateUseCase,
    private val getDailyRewardUseCase: GetDailyRewardUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val markTutorialShownUseCase: MarkTutorialShownUseCase,
) {

    fun create(): MenuStore =
        object : MenuStore, Store<Intent, State, Label> by storeFactory.create(
            name = "MenuStore",
            initialState = State(),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {

        data class GameChanged(val coins: Int, val bestScore: Int) : Action

        data class DailyRewardChanged(val reward: DailyReward) : Action

        data class RewardsChanged(val count: Int) : Action

        data object ShowTutorial : Action
    }

    private sealed interface Msg {

        data class GameChanged(val coins: Int, val bestScore: Int) : Msg

        data class DailyRewardChanged(val giftReady: Boolean, val streakDay: Int) : Msg

        data class RewardsChanged(val count: Int) : Msg

        data class GiftReceived(val gift: List<BonusItem>?) : Msg

        data class TutorialVisible(val visible: Boolean) : Msg
    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                observeGameUseCase()
                    // Рекорд в меню учитывает и текущую партию, если она уже его побила
                    .map { it.coins to maxOf(it.bestScore, it.totalScore) }
                    .distinctUntilChanged()
                    .collect { (coins, best) -> dispatch(Action.GameChanged(coins, best)) }
            }
            scope.launch {
                observeDailyRewardUseCase()
                    .distinctUntilChanged()
                    .collect { dispatch(Action.DailyRewardChanged(it)) }
            }
            scope.launch {
                observeAchievementStateUseCase().collect {
                    dispatch(Action.RewardsChanged(countRewardsToClaim()))
                }
            }
            scope.launch {
                if (!getSettingsUseCase().isTutorialShown) dispatch(Action.ShowTutorial)
            }
        }
    }

    private suspend fun countRewardsToClaim(): Int = try {
        getAchievementsUseCase().count { !it.isRewardClaimed && it.totalCount >= it.requiredCount }
    } catch (e: Exception) {
        0
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        // Защита от двойного тапа по подарку
        private var claimingGift = false

        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                // Ежедневный подарок выдаётся прямо в меню, так же как в магазине: монеты по дню серии, в 7-й день ещё бонус
                Intent.ClickGift -> {
                    if (claimingGift) return
                    claimingGift = true
                    scope.launch {
                        try {
                            if (getDailyRewardIsReceivedStateUseCase()) {
                                publish(Label.GiftAlreadyReceived)
                            } else {
                                dispatch(Msg.GiftReceived(getDailyRewardUseCase()))
                            }
                        } finally {
                            claimingGift = false
                        }
                    }
                }
                Intent.GiftConfirm -> dispatch(Msg.GiftReceived(null))
                // Закрытое обучение (пропуском или до конца) больше само не открывается
                Intent.TutorialClose -> {
                    dispatch(Msg.TutorialVisible(false))
                    scope.launch { markTutorialShownUseCase() }
                }
                Intent.ClickNavigateToAchievements -> publish(Label.ClickNavigateToAchievements)
                Intent.ClickNavigateToGame -> publish(Label.ClickNavigateToGame)
                Intent.ClickNavigateToShop -> publish(Label.ClickNavigateToShop)
                Intent.ClickNavigateToSettings -> publish(Label.ClickNavigateToSettings)
                // Прогресс достижений меняется в игре, поэтому пересчитываем при возврате в меню
                Intent.Refresh -> {
                    scope.launch {
                        dispatch(Msg.RewardsChanged(countRewardsToClaim()))
                    }
                }
            }
        }

        override fun executeAction(action: Action, getState: () -> State) {
            when (action) {
                is Action.GameChanged -> dispatch(Msg.GameChanged(action.coins, action.bestScore))
                is Action.RewardsChanged -> dispatch(Msg.RewardsChanged(action.count))
                Action.ShowTutorial -> dispatch(Msg.TutorialVisible(true))
                is Action.DailyRewardChanged -> {
                    val claimedToday = action.reward.isClaimedToday
                    val claimedInWeek = action.reward.currentStreak.claimedInWeek(claimedToday)
                    dispatch(
                        Msg.DailyRewardChanged(
                            giftReady = !claimedToday,
                            streakDay = if (claimedToday) claimedInWeek else claimedInWeek + 1
                        )
                    )
                }
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State = when (msg) {
            is Msg.GameChanged -> copy(coins = msg.coins, bestScore = msg.bestScore)
            is Msg.DailyRewardChanged -> copy(giftReady = msg.giftReady, streakDay = msg.streakDay)
            is Msg.RewardsChanged -> copy(rewardsToClaim = msg.count)
            is Msg.GiftReceived -> copy(receivedGift = msg.gift)
            is Msg.TutorialVisible -> copy(showTutorial = msg.visible)
        }
    }
}

// Сколько дней текущей недели серии уже забрано (серия идёт кругами по 7 дней)
fun Int.claimedInWeek(claimedToday: Boolean): Int =
    if (claimedToday) ((this - 1).coerceAtLeast(0) % 7) + 1 else this % 7

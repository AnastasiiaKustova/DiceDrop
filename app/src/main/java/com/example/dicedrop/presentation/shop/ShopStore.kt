package com.example.dicedrop.presentation.shop

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.dicedrop.core.DailyRewardReceived
import com.example.dicedrop.core.NotEnoughCoinsException
import com.example.dicedrop.domain.entity.BonusItem
import com.example.dicedrop.domain.entity.CoinPack
import com.example.dicedrop.domain.entity.ShopItem
import com.example.dicedrop.domain.useCases.DateHelper
import com.example.dicedrop.domain.useCases.game.ObserveGameUseCase
import com.example.dicedrop.domain.useCases.shop.BuyBonusUseCase
import com.example.dicedrop.domain.useCases.shop.GetDailyRewardIsReceivedStateUseCase
import com.example.dicedrop.domain.useCases.shop.GetDailyRewardUseCase
import com.example.dicedrop.domain.useCases.shop.GetRandomBonusUseCase
import com.example.dicedrop.domain.useCases.shop.GetShopItemsUseCase
import com.example.dicedrop.domain.useCases.shop.ObserveDailyRewardUseCase
import com.example.dicedrop.presentation.shop.ShopStore.Intent
import com.example.dicedrop.presentation.shop.ShopStore.Label
import com.example.dicedrop.presentation.shop.ShopStore.State
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

interface ShopStore : Store<Intent, State, Label> {

    sealed interface Intent {

        data object ClickBack : Intent

        data object RandomBonusConfirmClick : Intent

        data class ClickBuyItem(val shopItem: ShopItem) : Intent

        data class ClickBuyCoinPack(val coinPack: CoinPack) : Intent
    }

    data class State(
        val isDailyRewardReceived: Boolean,
        // Сколько дней подряд забран подарок; приходит отдельно от данных магазина
        val dayStreak: Int = 0,
        val shopState: ShopState
    ) {
        sealed interface ShopState {
            data object Initial : ShopState

            data object Loading : ShopState

            data object Error : ShopState

            data class GetRandomBonus(val bonus: BonusItem, val prevState: Loaded) : ShopState

            data class Loaded(
                val money: Int,
                val shopItems: List<ShopItem>,
                val dailyRewardClaimed: Boolean,
                // Сколько бонусов каждого типа уже есть у игрока
                val bonuses: List<BonusItem> = emptyList()
            ) : ShopState
        }
    }

    sealed interface Label {
        data object ClickBack : Label

        data object DailyRewardError : Label

        data object DailyRewardSuccess : Label

        data object NotEnoughCoins : Label

        data class ClickBuyItem(val shopItem: ShopItem) : Label

        data class ClickBuyCoinPack(val coinPack: CoinPack) : Label
    }
}

class ShopStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val observeGameUseCase: ObserveGameUseCase,
    private val observeDailyRewardUseCase: ObserveDailyRewardUseCase,
    private val buyBonusUseCase: BuyBonusUseCase,
    private val getRandomBonusUseCase: GetRandomBonusUseCase,
    private val getShopItemsUseCase: GetShopItemsUseCase,
    private val getDailyRewardUseCase: GetDailyRewardUseCase,
    private val getDailyRewardIsReceivedStateUseCase: GetDailyRewardIsReceivedStateUseCase
) {

    fun create(): ShopStore =
        object : ShopStore, Store<Intent, State, Label> by storeFactory.create(
            name = "ShopStore",
            initialState = State(
                isDailyRewardReceived = false,
                shopState = State.ShopState.Initial
            ),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {

        data class ShopLoaded(
            val money: Int,
            val bonuses: List<BonusItem>,
            val shopItems: List<ShopItem>,
            val dailyRewardClaimed: Boolean
        ) : Action

        data class ObserveDailyReward(
            val isDailyRewardReceived: Boolean,
            val dayStreak: Int
        ) : Action

        data class GetRandomBonus(val bonus: BonusItem) : Action

        data object ShopStartLoading : Action

        data object ShopLoadingError : Action
    }

    private sealed interface Msg {

        data class ShopLoaded(
            val money: Int,
            val bonuses: List<BonusItem>,
            val shopItems: List<ShopItem>,
            val dailyRewardClaimed: Boolean
        ) : Msg

        data class GetRandomBonus(val bonus: BonusItem) : Msg

        data class ObserveDailyReward(
            val isDailyRewardReceived: Boolean,
            val dayStreak: Int
        ) : Msg

        data object ReturnToShopLoaded : Msg

        data object ShopStartLoading : Msg

        data object ShopLoadingError : Msg

        data object DailyRewardError : Msg
    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                dispatch(Action.ShopStartLoading)
            }

            scope.launch {
                try {
                    observeGameUseCase()
                        .map { it.coins to it.bonuses }
                        .distinctUntilChanged()
                        .collect { (coins, bonuses) ->
                            val shopItems = getShopItemsUseCase()
                            val dailyRewardClaimed = getDailyRewardIsReceivedStateUseCase()
                            dispatch(
                                Action.ShopLoaded(
                                    money = coins,
                                    bonuses = bonuses,
                                    shopItems = shopItems,
                                    dailyRewardClaimed = dailyRewardClaimed
                                )
                            )
                        }
                } catch (e: Exception) {
                    dispatch(Action.ShopLoadingError)
                }
            }

            scope.launch {
                try {
                    observeDailyRewardUseCase()
                        .distinctUntilChanged()
                        .collect { dailyReward ->
                            val isDailyRewardReceived = dailyReward.lastDate == DateHelper.getCurrentDate()
                            dispatch(
                                Action.ObserveDailyReward(
                                    isDailyRewardReceived = isDailyRewardReceived,
                                    dayStreak = dailyReward.dayStreak
                                )
                            )
                        }
                } catch (e: Exception) {
                    dispatch(Action.ShopLoadingError)
                }
            }
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                Intent.ClickBack -> publish(Label.ClickBack)
                is Intent.ClickBuyCoinPack -> publish(Label.ClickBuyCoinPack(intent.coinPack))
                is Intent.ClickBuyItem -> {
                    scope.launch {
                        try {

                            if (intent.shopItem.bonusType == null) {
                                if (intent.shopItem.id == "daily_bonus") {
                                    val isDailyRewardReceived =
                                        getDailyRewardIsReceivedStateUseCase()
                                    if (isDailyRewardReceived) throw DailyRewardReceived("Daily reward already received")
                                    else getDailyRewardUseCase()
                                }

                                val randomBonus = getRandomBonusUseCase()
                                buyBonusUseCase(
                                    bonusType = randomBonus.type,
                                    price = intent.shopItem.price
                                )
                                dispatch(Msg.GetRandomBonus(randomBonus))
                                publish(
                                    if (intent.shopItem.id == "daily_bonus") Label.DailyRewardSuccess
                                    else Label.ClickBuyItem(intent.shopItem)
                                )
                            } else {
                                buyBonusUseCase(
                                    bonusType = intent.shopItem.bonusType,
                                    price = intent.shopItem.price
                                )
                                dispatch(
                                    Msg.GetRandomBonus(
                                        BonusItem(
                                            type = intent.shopItem.bonusType,
                                            count = 1
                                        )
                                    )
                                )
                                publish(Label.ClickBuyItem(intent.shopItem))
                            }
                        } catch (e: NotEnoughCoinsException) {
                            publish(Label.NotEnoughCoins)
                        } catch (e: DailyRewardReceived) {
                            publish(Label.DailyRewardError)
                        }
                    }
                }

                Intent.RandomBonusConfirmClick -> {
                    dispatch(Msg.ReturnToShopLoaded)
                }
            }
        }

        override fun executeAction(action: Action, getState: () -> State) {
            when (action) {
                is Action.ShopLoaded -> dispatch(
                    Msg.ShopLoaded(
                        money = action.money,
                        bonuses = action.bonuses,
                        shopItems = action.shopItems,
                        dailyRewardClaimed = action.dailyRewardClaimed
                    )
                )

                is Action.ObserveDailyReward -> dispatch(
                    Msg.ObserveDailyReward(
                        isDailyRewardReceived = action.isDailyRewardReceived,
                        dayStreak = action.dayStreak
                    )
                )

                Action.ShopLoadingError -> dispatch(Msg.ShopLoadingError)
                Action.ShopStartLoading -> dispatch(Msg.ShopStartLoading)
                is Action.GetRandomBonus -> dispatch(Msg.GetRandomBonus(action.bonus))
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State {
            return when (msg) {
                is Msg.ShopLoaded -> {
                    val current = shopState
                    val loaded = State.ShopState.Loaded(
                        money = msg.money,
                        shopItems = msg.shopItems,
                        dailyRewardClaimed = msg.dailyRewardClaimed,
                        bonuses = msg.bonuses
                    )
                    // Пока открыто окно полученного бонуса, обновляем данные под ним, не закрывая окно
                    when (current) {
                        is State.ShopState.GetRandomBonus -> copy(shopState = current.copy(prevState = loaded))
                        else -> copy(shopState = loaded)
                    }
                }

                is Msg.GetRandomBonus -> {
                    val loaded = shopState as? State.ShopState.Loaded
                        ?: return this

                    copy(
                        shopState = State.ShopState.GetRandomBonus(
                            bonus = msg.bonus,
                            prevState = loaded
                        )
                    )
                }

                is Msg.ObserveDailyReward -> {
                    val updated = copy(
                        isDailyRewardReceived = msg.isDailyRewardReceived,
                        dayStreak = msg.dayStreak
                    )
                    when (val current = shopState) {
                        is State.ShopState.Loaded -> updated.copy(
                            shopState = current.copy(dailyRewardClaimed = msg.isDailyRewardReceived)
                        )

                        is State.ShopState.GetRandomBonus -> updated.copy(
                            shopState = current.copy(
                                prevState = current.prevState.copy(dailyRewardClaimed = msg.isDailyRewardReceived)
                            )
                        )

                        else -> updated
                    }
                }

                Msg.ReturnToShopLoaded -> {
                    val loaded = shopState as? State.ShopState.GetRandomBonus
                        ?: return this

                    copy(shopState = loaded.prevState)
                }

                Msg.ShopLoadingError -> copy(shopState = State.ShopState.Error)
                Msg.ShopStartLoading -> copy(shopState = State.ShopState.Loading)
                Msg.DailyRewardError -> this
            }
        }
    }
}

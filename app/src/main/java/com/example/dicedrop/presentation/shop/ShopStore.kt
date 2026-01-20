package com.example.dicedrop.presentation.shop

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.dicedrop.domain.entity.BonusItem
import com.example.dicedrop.domain.entity.CoinPack
import com.example.dicedrop.domain.entity.ShopItem
import com.example.dicedrop.domain.useCases.shop.BuyBonusUseCase
import com.example.dicedrop.domain.useCases.game.ObserveCoinsValueUseCase
import com.example.dicedrop.domain.useCases.shop.GetDailyRewardIsReceivedStateUseCase
import com.example.dicedrop.domain.useCases.shop.GetDailyRewardUseCase
import com.example.dicedrop.domain.useCases.shop.GetRandomBonusUseCase
import com.example.dicedrop.domain.useCases.shop.GetShopItemsUseCase
import com.example.dicedrop.presentation.shop.ShopStore.Intent
import com.example.dicedrop.presentation.shop.ShopStore.Label
import com.example.dicedrop.presentation.shop.ShopStore.State
import kotlinx.coroutines.flow.distinctUntilChanged
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
        val shopState: ShopState
    ) {
        sealed interface ShopState {
            data object Initial : ShopState

            data object Loading : ShopState

            data object Error : ShopState

            data class GetRandomBonus(val bonus: BonusItem, val prevState: Loaded) : ShopState

            //data object Error : ShopState

            data class Loaded(val money: Int, val shopItems: List<ShopItem>, val dailyRewardClaimed: Boolean) : ShopState
        }
    }

    sealed interface Label {
        data object ClickBack : Label

        data object DailyRewardError : Label

        data object DailyRewardSuccess : Label

        data class Error(val message: String) : Label

        data class ClickBuyItem(val shopItem: ShopItem) : Label

        data class ClickBuyCoinPack(val coinPack: CoinPack) : Label
    }
}

class ShopStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val observeCoinsValueUseCase: ObserveCoinsValueUseCase,
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

        data class ShopLoaded(val money: Int, val shopItems: List<ShopItem>, val dailyRewardClaimed: Boolean) : Action

        data class GetRandomBonus(val bonus: BonusItem) : Action

        data object ShopStartLoading : Action

        data object ShopLoadingError : Action
    }

    private sealed interface Msg {

        data class ShopLoaded(val money: Int, val shopItems: List<ShopItem>, val dailyRewardClaimed: Boolean) : Msg

        data class GetRandomBonus(val bonus: BonusItem) : Msg

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
                    observeCoinsValueUseCase()
                        .distinctUntilChanged()
                        .collect { coins ->
                            val shopItems = getShopItemsUseCase()
                            val dailyRewardClaimed = getDailyRewardIsReceivedStateUseCase()
                            dispatch(
                                Action.ShopLoaded(
                                    money = coins,
                                    shopItems = shopItems,
                                    dailyRewardClaimed = dailyRewardClaimed
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
                                val randomBonus = getRandomBonusUseCase()
                                buyBonusUseCase(
                                    intent.shopItem.copy(
                                        bonusType = randomBonus.type
                                    )
                                )
                                dispatch(Msg.GetRandomBonus(randomBonus))
                            } else {
                                buyBonusUseCase(intent.shopItem)
                                dispatch(
                                    Msg.GetRandomBonus(
                                        BonusItem(
                                            type = intent.shopItem.bonusType,
                                            count = 1
                                        )
                                    )
                                )
                            }
                        } catch (e: Exception) {
                            publish(Label.Error(e.message ?: "Unknown error"))
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
                        shopItems = action.shopItems,
                        dailyRewardClaimed = action.dailyRewardClaimed
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
                is Msg.ShopLoaded -> copy(
                    shopState = State.ShopState.Loaded(
                        money = msg.money,
                        shopItems = msg.shopItems,
                        dailyRewardClaimed = msg.dailyRewardClaimed
                    )
                )

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

                Msg.ReturnToShopLoaded -> {
                    val loaded = shopState as? State.ShopState.GetRandomBonus
                        ?: return this

                    val prevState = loaded.prevState
                    copy(
                        shopState = State.ShopState.Loaded(
                            money = prevState.money,
                            shopItems = prevState.shopItems,
                            dailyRewardClaimed = prevState.dailyRewardClaimed
                        )
                    )
                }

                Msg.ShopLoadingError -> copy(shopState = State.ShopState.Error)
                Msg.ShopStartLoading -> copy(shopState = State.ShopState.Loading)
                Msg.DailyRewardError -> this
            }
        }
    }
}
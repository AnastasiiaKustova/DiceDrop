package com.example.dicedrop.presentation.shop

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.dicedrop.domain.entity.CoinPack
import com.example.dicedrop.domain.entity.ShopItem
import com.example.dicedrop.domain.useCases.shop.BuyBonusUseCase
import com.example.dicedrop.domain.useCases.game.ObserveCoinsValueUseCase
import com.example.dicedrop.domain.useCases.shop.GetDailyRewardIsReceivedStateUseCase
import com.example.dicedrop.domain.useCases.shop.GetDailyRewardUseCase
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

        data object ClickGetDailyReward : Intent

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

            data class Loaded(val money: Int, val shopItems: List<ShopItem>) : ShopState
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

        data class ShopLoaded(val money: Int, val shopItems: List<ShopItem>) : Action

        data object ShopStartLoading : Action

        data object ShopLoadingError : Action
    }

    private sealed interface Msg {

        data class DailyRewardReceived(val isDailyRewardReceived: Boolean) : Msg

        data class ShopLoaded(val money: Int, val shopItems: List<ShopItem>) : Msg

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
                            dispatch(
                                Action.ShopLoaded(
                                    money = coins,
                                    shopItems = shopItems
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
                            buyBonusUseCase(intent.shopItem)
                            publish(Label.ClickBuyItem(intent.shopItem))
                        } catch (e: Exception) {
                            publish(Label.Error(e.message ?: "Unknown error"))
                        }
                    }
                }

                Intent.ClickGetDailyReward -> {
                    scope.launch {
                        try {
                            val isDailyRewardReceived = getDailyRewardIsReceivedStateUseCase()
                            if (!isDailyRewardReceived) {
                                getDailyRewardUseCase()
                                dispatch(Msg.DailyRewardReceived(true))
                            }
                            publish(Label.DailyRewardSuccess)
                        } catch (e: Exception) {
                            publish(Label.DailyRewardError)
                        }
                    }
                }
            }
        }

        override fun executeAction(action: Action, getState: () -> State) {
            when (action) {
                is Action.ShopLoaded -> dispatch(
                    Msg.ShopLoaded(
                        money = action.money,
                        shopItems = action.shopItems
                    )
                )

                Action.ShopLoadingError -> dispatch(Msg.ShopLoadingError)
                Action.ShopStartLoading -> dispatch(Msg.ShopStartLoading)
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State = when (msg) {
            is Msg.DailyRewardReceived -> copy(isDailyRewardReceived = msg.isDailyRewardReceived)
            is Msg.ShopLoaded -> copy(
                shopState = State.ShopState.Loaded(
                    money = msg.money,
                    shopItems = msg.shopItems
                )
            )

            Msg.ShopLoadingError -> copy(shopState = State.ShopState.Error)
            Msg.ShopStartLoading -> copy(shopState = State.ShopState.Loading)
            Msg.DailyRewardError -> this
        }
    }
}
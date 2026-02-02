package com.example.dicedrop.presentation.shop

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.dicedrop.core.componentScope
import com.example.dicedrop.domain.entity.CoinPack
import com.example.dicedrop.domain.entity.ShopItem
import com.example.dicedrop.presentation.shop.ShopComponent.UiEvent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ShopComponentImpl @AssistedInject constructor(
    private val storeFactory: ShopStoreFactory,
    @Assisted("onBackClicked") private val onBackClicked: () -> Unit,
    @Assisted("componentContext") componentContext: ComponentContext,
): ShopComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore { storeFactory.create() }
    private val scope = componentScope()

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    override val uiEvents: SharedFlow<UiEvent> = _uiEvents

    init {
        scope.launch {
            store.labels.collect {
                when (it) {
                    ShopStore.Label.ClickBack -> onBackClicked()
                    is ShopStore.Label.ClickBuyCoinPack -> _uiEvents.emit(UiEvent.ShowMessage(UiEvent.MessageType.PurchaseSuccess))
                    is ShopStore.Label.ClickBuyItem -> _uiEvents.emit(UiEvent.ShowMessage(UiEvent.MessageType.PurchaseSuccess))
                    ShopStore.Label.DailyRewardError -> _uiEvents.emit(UiEvent.ShowError(UiEvent.ErrorType.DailyBonusFailed))
                    ShopStore.Label.DailyRewardSuccess -> _uiEvents.emit(UiEvent.ShowMessage(UiEvent.MessageType.DailyBonusClaimed))
                    is ShopStore.Label.Error -> _uiEvents.emit(UiEvent.ShowError(error = UiEvent.ErrorType.BuyBonusFailed, message = it.message))
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<ShopStore.State> = store.stateFlow

    override fun onBackClick() {
        store.accept(ShopStore.Intent.ClickBack)
    }

    override fun onBuyItemClick(shopItem: ShopItem) {
        store.accept(ShopStore.Intent.ClickBuyItem(shopItem))
    }

    override fun onBuyCoinPackClick(coinPack: CoinPack) {
        store.accept(ShopStore.Intent.ClickBuyCoinPack(coinPack))
    }

    override fun onRandomBonusConfirmClick(){
        store.accept(ShopStore.Intent.RandomBonusConfirmClick)
    }

    @AssistedFactory
    interface Factory{
        fun create(
            @Assisted("onBackClicked") onBackClicked: () -> Unit,
            @Assisted("componentContext") componentContext: ComponentContext,
        ) : ShopComponentImpl
    }
}
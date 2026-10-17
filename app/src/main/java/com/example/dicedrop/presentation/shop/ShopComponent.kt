package com.example.dicedrop.presentation.shop


import com.example.dicedrop.domain.entity.CoinPack
import com.example.dicedrop.domain.entity.ShopItem
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface ShopComponent {
    val model: StateFlow<ShopStore.State>
    val uiEvents: SharedFlow<UiEvent>

    fun onBackClick()

    fun onRandomBonusConfirmClick()

    fun onBuyItemClick(shopItem: ShopItem)

    fun onBuyCoinPackClick(coinPack: CoinPack)

    sealed interface UiEvent {
        data class ShowMessage(val message: MessageType) : UiEvent
        data class ShowError(val error: ErrorType, val message: String? = null) : UiEvent

        enum class MessageType {
            PurchaseSuccess,
            DailyBonusClaimed
        }

        enum class ErrorType {
            NotEnoughCoins,
            BuyBonusFailed,
            PurchaseFailed,
            DailyBonusUnavailable,
            DailyBonusFailed,
        }
    }
}
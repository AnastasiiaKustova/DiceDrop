package com.example.dicedrop.presentation.shop

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.dicedrop.R
import com.example.dicedrop.presentation.game.ui.GameSnackbarHost
import com.example.dicedrop.presentation.shop.ui.RandomBonusDialog
import com.example.dicedrop.presentation.shop.ui.ShopItems
import com.example.dicedrop.presentation.shop.ui.ShopTopBar
import com.example.dicedrop.presentation.ui.theme.LightBlue
import kotlinx.coroutines.launch

@Composable
fun ShopContent(component: ShopComponent) {
    val state by component.model.collectAsState()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val coins = when (val shopState = state.shopState) {
        is ShopStore.State.ShopState.Loaded -> shopState.money
        is ShopStore.State.ShopState.GetRandomBonus -> shopState.prevState.money
        else -> 0
    }

    LaunchedEffect(Unit) {
        component.uiEvents.collect { event ->
            val messageRes = when (event) {
                is ShopComponent.UiEvent.ShowError -> when (event.error) {
                    ShopComponent.UiEvent.ErrorType.NotEnoughCoins -> R.string.not_enough_coins
                    ShopComponent.UiEvent.ErrorType.DailyBonusFailed -> R.string.daily_gift_already_received
                    else -> null
                }
                is ShopComponent.UiEvent.ShowMessage -> null
            }
            if (messageRes != null) {
                // Снэкбар висит несколько секунд, не блокируем им следующие события
                launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(
                        message = context.getString(messageRes),
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }
    Scaffold(
        topBar = {
            ShopTopBar(
                coinsValue = coins,
                onBackClick = component::onBackClick
            )
        },
        snackbarHost = { GameSnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize(),
        containerColor = LightBlue
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val state = state.shopState) {
                ShopStore.State.ShopState.Error -> Error()
                ShopStore.State.ShopState.Initial -> Loading()
                ShopStore.State.ShopState.Loading -> Loading()

                is ShopStore.State.ShopState.GetRandomBonus,
                is ShopStore.State.ShopState.Loaded -> {

                    val shopState = when (state) {
                        is ShopStore.State.ShopState.Loaded -> state
                        is ShopStore.State.ShopState.GetRandomBonus -> state.prevState
                        else -> error("Impossible")
                    }

                    ShopItems(
                        dailyRewardClaimed = shopState.dailyRewardClaimed,
                        onCardClick = { component.onBuyItemClick(it) }
                    )
                }
            }
        }
    }

    when (val state = state.shopState) {
        is ShopStore.State.ShopState.GetRandomBonus ->
            RandomBonusDialog(
                bonus = state.bonus,
                confirmClick = component::onRandomBonusConfirmClick
            )

        else -> Unit
    }
}

@Composable
private fun Error() {

}

@Composable
private fun Loading() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            color = Color.White
        )
    }
}
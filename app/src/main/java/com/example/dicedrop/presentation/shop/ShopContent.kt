package com.example.dicedrop.presentation.shop

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.dicedrop.domain.entity.BonusItem
import com.example.dicedrop.presentation.game.GameStore
import com.example.dicedrop.presentation.game.ui.popups.NewRecordDialog
import com.example.dicedrop.presentation.shop.ui.RandomBonusDialog
import com.example.dicedrop.presentation.shop.ui.ShopItems
import com.example.dicedrop.presentation.shop.ui.ShopTopBar
import com.example.dicedrop.presentation.ui.theme.LightBlue

@Composable
fun ShopContent(component: ShopComponent) {
    val state by component.model.collectAsState()

    val coins = remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        component.uiEvents.collect { event ->
            when (event) {
                is ShopComponent.UiEvent.ShowError -> {}
                is ShopComponent.UiEvent.ShowMessage -> {}
            }
        }
    }
    Scaffold(
        topBar = {
            ShopTopBar(
                coinsValue = coins.value,
                onBackClick = component::onBackClick
            )
        },
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

                    coins.value = shopState.money
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
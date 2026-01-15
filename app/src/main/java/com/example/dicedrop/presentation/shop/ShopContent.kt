package com.example.dicedrop.presentation.shop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.dicedrop.R
import com.example.dicedrop.domain.catalog.ShopItemCatalog
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.domain.entity.ShopItem
import com.example.dicedrop.presentation.game.ui.bottompanel.getIconResId
import com.example.dicedrop.presentation.shop.ui.BonusCard
import com.example.dicedrop.presentation.ui.theme.WhiteBlue

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
            TopBar(
                coinsValue = coins.value,
                onBackClick = component::onBackClick
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val shopState = state.shopState) {
                ShopStore.State.ShopState.Error -> {}
                ShopStore.State.ShopState.Initial -> {}
                is ShopStore.State.ShopState.Loaded -> {
                    ShopItems(
                        onCardClick = {component.onBuyItemClick(it)}
                    )
                    coins.value = shopState.money
                }

                ShopStore.State.ShopState.Loading -> {}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    coinsValue: Int,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
) {
    CenterAlignedTopAppBar(
        modifier = modifier
            .background(color = WhiteBlue),
        title = { Text(text = "Shop") },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors().copy(
            containerColor = Color.White
        ),
        navigationIcon = {
            IconButton(onClick = { onBackClick() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.background
                )
            }
        },
        actions = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 12.dp)
            ) {
                Text(
                    text = coinsValue.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(4.dp))

                Icon(
                    painter = painterResource(R.drawable.coin),
                    contentDescription = null,
                    tint = Color.Unspecified, // чтобы иконка не перекрашивалась
                    modifier = Modifier.size(24.dp)
                )
            }
        }

    )
}

@Composable
fun ShopItems(
    onCardClick: (ShopItem) -> Unit
) {

    val shopItems = ShopItemCatalog.all


    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Bonuses")

        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize()
                .heightIn(min = 0.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(8.dp),
            columns = GridCells.Adaptive(minSize = 150.dp)
        ) {

            items(
                items = shopItems,
                key = { it.id }
            ) { item ->
                BonusCard(
                    title = item.title,
                    iconResId = item.iconResId,
                    price = item.price,
                    onCardClick = { onCardClick(item) }
                )
            }
        }
    }
}
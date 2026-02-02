package com.example.dicedrop.presentation.shop.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dicedrop.domain.catalog.ShopItemCatalog
import com.example.dicedrop.domain.entity.ShopItem

@Composable
fun ShopItems(
    dailyRewardClaimed: Boolean,
    onCardClick: (ShopItem) -> Unit
) {

    val shopItems = ShopItemCatalog.all

    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 0.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(8.dp),
            columns = GridCells.Adaptive(minSize = 100.dp)
        ) {

            items(
                items = shopItems,
                key = { it.id }
            ) { item ->
                BonusCard(
                    bonusId = item.id,
                    title = item.title,
                    iconResId = item.iconResId,
                    price = item.price,
                    dailyRewardClaimed = dailyRewardClaimed,
                    onCardClick = { onCardClick(item) }
                )
            }
        }

        /*Box(
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth()
                .background(color = Color.White, shape = RoundedCornerShape(12.dp))
                .border(
                    width = 2.dp,
                    color = DarkBlue,
                    shape = RoundedCornerShape(12.dp)
                ),
            ) {
        }*/
    }
}
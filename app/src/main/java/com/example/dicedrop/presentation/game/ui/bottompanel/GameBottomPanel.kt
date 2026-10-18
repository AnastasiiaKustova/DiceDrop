package com.example.dicedrop.presentation.game.ui.bottompanel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dicedrop.domain.entity.BonusItem
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.presentation.ui.theme.DeepBlue

// Порядок бонусов в панели, как в магазине
private val panelOrder = listOf(BonusType.ROTATE, BonusType.FREEZE, BonusType.LUCKY, BonusType.DRAFT)

@Composable
fun GameBottomPanel(
    modifier: Modifier = Modifier,
    bonusStatistic: List<BonusItem>,
    isLandscape: Boolean,
    freezeEnabled: Boolean,
    spareActive: Boolean,
    luckyEnabled: Boolean,
    onBuyClick: (BonusType) -> Unit,
    onBonusClick: (BonusType) -> Unit,
) {
    val bonuses = panelOrder.mapNotNull { type -> bonusStatistic.firstOrNull { it.type == type } }

    // itemModifier приходит из Row/Column, чтобы кнопки делили место поровну
    val content = @Composable { itemModifier: Modifier ->
        bonuses.forEach { bonus ->
            Box(
                modifier = itemModifier,
                contentAlignment = Alignment.Center
            ) {
                BonusButton(
                    iconResId = bonus.type.getIconResId(),
                    count = bonus.count,
                    price = bonus.type.getPrice(),
                    enabled = when (bonus.type) {
                        BonusType.FREEZE -> freezeEnabled && !spareActive
                        BonusType.ROTATE, BonusType.DRAFT -> !spareActive
                        BonusType.LUCKY -> !spareActive && luckyEnabled
                        else -> true
                    },
                    onBonusClick = { onBonusClick(bonus.type) },
                    onBuyClick = { onBuyClick(bonus.type) }
                )
            }
        }
    }

    if (isLandscape) {
        Column(
            modifier = modifier
                .background(DeepBlue)
                .fillMaxHeight()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content(Modifier.weight(1f))
        }
    } else {
        Row(
            modifier = modifier
                .background(DeepBlue)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            content(Modifier.weight(1f))
        }
    }
}

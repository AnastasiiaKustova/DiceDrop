package com.example.dicedrop.presentation.game.ui.bottompanel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import com.example.dicedrop.presentation.ui.theme.BlueGradient

@Composable
fun GameBottomPanel(
    modifier: Modifier = Modifier,
    bonusStatistic: List<BonusItem>,
    isLandscape: Boolean,
    freezeEnabled: Boolean,
    spareActive: Boolean,
    luckyEnabled: Boolean,
    onCounterClick: (BonusItem) -> Unit,
    onBonusClick: (BonusType) -> Unit,
) {
    val content = @Composable{
        bonusStatistic.forEach { bonus ->
            BonusButton(
                iconResId = bonus.type.getIconResId(),
                count = bonus.count,
                isLandscape = isLandscape,
                enabled = when (bonus.type) {
                    BonusType.FREEZE -> freezeEnabled && !spareActive
                    BonusType.ROTATE, BonusType.DRAFT -> !spareActive
                    BonusType.LUCKY -> !spareActive && luckyEnabled
                    else -> true
                },
                onBonusClick = {onBonusClick(bonus.type)},
                onCounterClick = { if (bonus.count <= 0) onCounterClick(bonus) }
            )
        }
    }

    if (isLandscape){
        Column(
            modifier = modifier
                .background(BlueGradient)
                .fillMaxHeight()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()
        }
    }
    else{
        Row(
            modifier = modifier
                .background(BlueGradient)
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            content()
        }
    }
}
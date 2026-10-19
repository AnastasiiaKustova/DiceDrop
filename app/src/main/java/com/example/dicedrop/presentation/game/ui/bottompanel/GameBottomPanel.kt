package com.example.dicedrop.presentation.game.ui.bottompanel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.domain.entity.BonusItem
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.presentation.ui.theme.DeepBlue
import com.example.dicedrop.presentation.ui.theme.White

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

    Row(
        modifier = modifier
            .background(DeepBlue)
            .fillMaxWidth()
            .then(
                if (isLandscape) Modifier.padding(start = 8.dp, end = 8.dp, top = 22.dp, bottom = 24.dp)
                else Modifier.padding(horizontal = 12.dp)
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        bonuses.forEach { bonus ->
            // Кнопки делят ширину поровну
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
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
                // На планшете места хватает, поэтому под кнопками подписаны названия бонусов
                if (isLandscape) {
                    Text(
                        modifier = Modifier.alpha(0.85f),
                        text = stringResource(bonus.type.getTitleResId()),
                        color = White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

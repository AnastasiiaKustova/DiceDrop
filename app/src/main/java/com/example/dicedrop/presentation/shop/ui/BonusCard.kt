package com.example.dicedrop.presentation.shop.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.presentation.common.GameButton
import com.example.dicedrop.presentation.common.IconCircle
import com.example.dicedrop.presentation.common.PriceTag
import com.example.dicedrop.presentation.game.ui.bottompanel.getDescriptionResId
import com.example.dicedrop.presentation.game.ui.bottompanel.getIconResId
import com.example.dicedrop.presentation.game.ui.bottompanel.getTitleResId
import com.example.dicedrop.presentation.ui.theme.MidBlue
import com.example.dicedrop.presentation.ui.theme.White

// Карточка бонуса в магазине: цена сверху, иконка, название, количество у игрока, описание и покупка.
// Высоту карточкам в ряду выравнивает родитель, кнопка всегда прижата к низу
@Composable
fun BonusCard(
    bonusType: BonusType,
    price: Int,
    owned: Int,
    modifier: Modifier = Modifier,
    onBuyClick: () -> Unit
) {
    Column(
        modifier = modifier
            .background(MidBlue, RoundedCornerShape(22.dp))
            .padding(start = 12.dp, end = 12.dp, top = 10.dp, bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            PriceTag(price = price)
        }
        Spacer(modifier = Modifier.height(6.dp))
        IconCircle(iconRes = bonusType.getIconResId(), size = 58.dp, iconSize = 43.dp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(bonusType.getTitleResId()),
            color = White,
            fontSize = 16.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        Text(
            modifier = Modifier.alpha(0.75f),
            text = "($owned)",
            color = White,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold
        )
        bonusType.getDescriptionResId()?.let {
            Text(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .alpha(0.85f),
                text = stringResource(it),
                color = White,
                fontSize = 12.sp,
                lineHeight = 15.sp,
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.weight(1f).height(10.dp))
        GameButton(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth(),
            text = stringResource(R.string.buy),
            onClick = onBuyClick,
            height = 36.dp,
            corner = 12.dp,
            fontSize = 16.sp
        )
    }
}

// Строка "Случайный бонус": цена в углу, как у карточек бонусов
@Composable
fun RandomBonusRow(
    price: Int,
    onBuyClick: () -> Unit
) {
    // Картинка и текст - по центру карточки по высоте; справа цена сверху, кнопка снизу
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(MidBlue, RoundedCornerShape(22.dp))
            .padding(start = 14.dp, end = 12.dp, top = 10.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IconCircle(iconRes = R.drawable.ic_random_die, size = 54.dp, iconSize = 40.dp)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.random_bonus_title),
                color = White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                softWrap = false
            )
            Text(
                modifier = Modifier.alpha(0.85f),
                text = stringResource(R.string.random_bonus_desc),
                color = White,
                fontSize = 12.sp,
                lineHeight = 15.sp
            )
        }
        Column(
            modifier = Modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            PriceTag(price = price)
            GameButton(
                modifier = Modifier.padding(top = 8.dp),
                text = stringResource(R.string.buy),
                onClick = onBuyClick,
                height = 36.dp,
                corner = 12.dp,
                fontSize = 16.sp
            )
        }
    }
}

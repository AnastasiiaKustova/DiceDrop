package com.example.dicedrop.presentation.shop.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
import com.example.dicedrop.presentation.common.CoinChip
import com.example.dicedrop.presentation.common.RoundImageButton
import com.example.dicedrop.presentation.common.isLandscape
import com.example.dicedrop.presentation.ui.theme.White

@Composable
fun ShopTopBar(
    coinsValue: Int,
    modifier: Modifier = Modifier,
    // Для счётчика монет: сюда летят монеты подарка
    coinsModifier: Modifier = Modifier,
    onBackClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            // На планшете поля шире, как у содержимого магазина
            .padding(horizontal = if (isLandscape()) 40.dp else 20.dp)
            .padding(top = 20.dp)
    ) {
        RoundImageButton(
            modifier = Modifier.align(Alignment.CenterStart),
            iconRes = R.drawable.btn_back,
            contentDescription = stringResource(R.string.back),
            size = 44.dp,
            onClick = onBackClick
        )
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = stringResource(R.string.store),
            color = White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Black
        )
        CoinChip(
            modifier = coinsModifier.align(Alignment.CenterEnd),
            coins = coinsValue,
            height = 36.dp,
            fontSize = 15.sp
        )
    }
}

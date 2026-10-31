package com.example.dicedrop.presentation.achievements.ui

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
import com.example.dicedrop.presentation.common.CoinChip
import com.example.dicedrop.presentation.common.RoundImageButton
import com.example.dicedrop.presentation.common.isLandscape
import com.example.dicedrop.presentation.ui.theme.White

// coins - null, пока баланс не загружен: место под плашку занято, чтобы заголовок не сдвигался.
// Заголовок длинный, поэтому по центру между кнопкой и плашкой: так он не наедет на большое число монет
@Composable
fun AchievementTopBar(
    modifier: Modifier = Modifier,
    coins: Int?,
    coinsModifier: Modifier = Modifier,
    onBackClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            // На планшете поля шире, как у содержимого
            .padding(horizontal = if (isLandscape()) 40.dp else 20.dp)
            .padding(top = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RoundImageButton(
            iconRes = R.drawable.btn_back,
            contentDescription = stringResource(R.string.back),
            size = 44.dp,
            onClick = onBackClick
        )
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.achievements),
            color = White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        CoinChip(
            modifier = coinsModifier.alpha(if (coins == null) 0f else 1f),
            coins = coins ?: 0,
            height = 36.dp,
            fontSize = 15.sp
        )
    }
}

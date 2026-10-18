package com.example.dicedrop.presentation.game.ui.bottompanel

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
import com.example.dicedrop.presentation.ui.theme.ButtonText
import com.example.dicedrop.presentation.ui.theme.CoinEdge
import com.example.dicedrop.presentation.ui.theme.DarkBlue
import com.example.dicedrop.presentation.ui.theme.White
import com.example.dicedrop.presentation.ui.theme.Yellow

// Круглая кнопка бонуса: со счётчиком, если бонусы есть, и с ценой, если закончились
@Composable
fun BonusButton(
    modifier: Modifier = Modifier,
    @DrawableRes iconResId: Int,
    count: Int,
    price: Int,
    enabled: Boolean = true,
    onBonusClick: () -> Unit,
    onBuyClick: () -> Unit
) {
    val has = count > 0

    Box(
        modifier = modifier
            .size(66.dp)
            // Прозрачность без отдельного слоя: слой размером с кнопку обрезал бы значок, торчащий за край
            .graphicsLayer {
                alpha = if (enabled) 1f else 0.45f
                compositingStrategy = CompositingStrategy.ModulateAlpha
            }
    ) {
        Box(
            modifier = Modifier
                .size(66.dp)
                .clip(CircleShape)
                .background(if (has) White else White.copy(alpha = 0.35f))
                .clickable(enabled = enabled) { if (has) onBonusClick() else onBuyClick() },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                modifier = Modifier
                    .size(46.dp)
                    .alpha(if (has) 1f else 0.45f)
            )
        }

        if (has) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 4.dp, y = 4.dp)
                    .size(28.dp)
                    .background(DarkBlue, CircleShape)
                    .border(3.dp, White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (count > 99) "99" else count.toString(),
                    color = White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 12.dp, y = 4.dp)
                    .height(28.dp)
                    .background(Yellow, CircleShape)
                    .border(2.dp, CoinEdge, CircleShape)
                    .padding(start = 2.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.coin_star),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = price.toString(),
                    color = ButtonText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

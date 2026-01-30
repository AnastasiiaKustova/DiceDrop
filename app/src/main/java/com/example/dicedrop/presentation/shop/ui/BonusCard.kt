package com.example.dicedrop.presentation.shop.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.presentation.game.ui.bottompanel.getIconResId
import com.example.dicedrop.presentation.ui.theme.BlackGradientDark
import com.example.dicedrop.presentation.ui.theme.BlackGradientLight
import com.example.dicedrop.presentation.ui.theme.DarkBlue
import com.example.dicedrop.presentation.ui.theme.LightBlue
import com.example.dicedrop.presentation.ui.theme.White

@Composable
fun BonusCard(
    bonusId: String,
    title: String,
    iconResId: Int,
    price: Int,
    modifier: Modifier = Modifier,
    dailyRewardClaimed: Boolean,
    onCardClick: () -> Unit
) {

    val isDailyRewardClaimed = (bonusId == "daily_bonus" && dailyRewardClaimed)

    Card(
        modifier = modifier

            .width(150.dp)
            .height(200.dp)

    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .background(Color.White)
                .fillMaxWidth()
                .weight(0.7f)
                .padding(
                    top = 20.dp,
                    start = 12.dp,
                    end = 12.dp,
                    bottom = 16.dp,
                )
        ) {

            Text(
                text = title,
                color = DarkBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            if (isDailyRewardClaimed) {
                Image(
                    modifier = Modifier.size(75.dp),
                    painter = painterResource(id = R.drawable.ic_gift_open),
                    contentDescription = null,
                )
            } else {
                Image(
                    modifier = Modifier.size(75.dp),
                    painter = painterResource(id = iconResId),
                    contentDescription = null,
                )
            }

        }

        Row(
            modifier = Modifier
                .background(Color.White)
                .padding(
                    start = 12.dp,
                    end = 12.dp,
                    bottom = 20.dp,
                )
                .background(
                    brush = if (isDailyRewardClaimed) {
                        Brush.linearGradient(
                            colors = listOf(BlackGradientDark, BlackGradientLight),
                            start = Offset(0f, 0f),
                            end = Offset(0f, Float.POSITIVE_INFINITY)
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(LightBlue, DarkBlue),
                            start = Offset(0f, 0f),
                            end = Offset(0f, Float.POSITIVE_INFINITY)
                        )
                    }, shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 2.dp,
                    brush = if (isDailyRewardClaimed) {
                        Brush.linearGradient(
                            colors = listOf(BlackGradientLight, BlackGradientDark),
                            start = Offset(0f, 0f),
                            end = Offset(0f, Float.POSITIVE_INFINITY)
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(DarkBlue, LightBlue),
                            start = Offset(0f, 0f),
                            end = Offset(0f, Float.POSITIVE_INFINITY)
                        )
                    },
                    shape = RoundedCornerShape(12.dp)
                )
                .fillMaxWidth()
                .weight(0.3f)
                .clickable { if (isDailyRewardClaimed) Unit else onCardClick() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (price == 0) {
                Text(
                    fontWeight = FontWeight.Bold,
                    fontSize = if (isDailyRewardClaimed) 16.sp else 21.sp,
                    text = if (isDailyRewardClaimed) "Claimed" else "Free",
                    color = White
                )

            } else {
                Image(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.coin),
                    contentDescription = null,
                )
                Spacer(Modifier.size(8.dp))
                Text(
                    fontWeight = FontWeight.Bold,
                    fontSize = 21.sp,
                    text = price.toString(),
                    color = White
                )
            }


        }
    }
}

@Composable
@Preview
fun test() {
    val bonus = BonusType.BOMB
    BonusCard(
        bonusId = "daily_bonus",
        title = "Bomb Bomb Bomb",
        iconResId = bonus.getIconResId(),
        price = 0,
        dailyRewardClaimed = true,
        onCardClick = {}
    )
}
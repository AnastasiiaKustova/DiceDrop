package com.example.dicedrop.presentation.shop.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
import com.example.dicedrop.domain.entity.BonusItem
import com.example.dicedrop.presentation.common.ConfettiBit
import com.example.dicedrop.presentation.common.ConfettiBurst
import com.example.dicedrop.presentation.common.DialogScrim
import com.example.dicedrop.presentation.common.GameButton
import com.example.dicedrop.presentation.common.IconCircle
import com.example.dicedrop.presentation.common.consumeClicks
import com.example.dicedrop.presentation.game.ui.bottompanel.getDescriptionResId
import com.example.dicedrop.presentation.game.ui.bottompanel.getRewardIconResId
import com.example.dicedrop.presentation.game.ui.bottompanel.getTitleResId
import com.example.dicedrop.presentation.ui.theme.ButtonText
import com.example.dicedrop.presentation.ui.theme.DarkBlue
import com.example.dicedrop.presentation.ui.theme.Green
import com.example.dicedrop.presentation.ui.theme.LightBlue
import com.example.dicedrop.presentation.ui.theme.MidBlue
import com.example.dicedrop.presentation.ui.theme.Orange
import com.example.dicedrop.presentation.ui.theme.White
import com.example.dicedrop.presentation.ui.theme.Yellow

@Composable
fun RandomBonusDialog(
    modifier: Modifier = Modifier,
    bonus: BonusItem,
    confirmClick: () -> Unit
) {
    // Окно "выпрыгивает" с лёгкой пружиной
    val appear = remember { Animatable(0.3f) }
    LaunchedEffect(Unit) {
        appear.animateTo(
            1f,
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
    }

    DialogScrim(onDismiss = confirmClick) {
        Box(
            modifier = modifier
                .padding(horizontal = 44.dp)
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = appear.value
                    scaleY = appear.value
                }
                .background(DarkBlue, RoundedCornerShape(30.dp))
                .consumeClicks()
        ) {
            ConfettiBit(x = 30.dp, y = 74.dp, size = 14.dp, color = Orange, rotation = 20f)
            ConfettiBit(x = 50.dp, y = 170.dp, size = 9.dp, color = Green, rotation = -15f)
            ConfettiBit(x = 34.dp, y = 84.dp, size = 12.dp, color = Yellow, rotation = 35f, alignRight = true)
            ConfettiBit(x = 48.dp, y = 186.dp, size = 8.dp, color = LightBlue, round = true, alignRight = true)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 22.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.alpha(0.85f),
                    text = stringResource(R.string.you_got).uppercase(),
                    color = White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Box {
                    // Конфетти вылетает из-за иконки полученного бонуса
                    ConfettiBurst(
                        key = Unit,
                        modifier = Modifier.matchParentSize(),
                        count = 48
                    )
                    IconCircle(
                        iconRes = bonus.type.getRewardIconResId(),
                        size = 132.dp,
                        iconSize = 96.dp,
                        ring = 8.dp,
                        ringColor = MidBlue
                    )
                    Text(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = (-4).dp, y = (-10).dp)
                            .background(Yellow, CircleShape)
                            .padding(horizontal = 10.dp, vertical = 3.dp),
                        text = "+${bonus.count}",
                        color = ButtonText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(bonus.type.getTitleResId()),
                    color = White,
                    fontSize = 30.sp,
                    lineHeight = 33.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
                bonus.type.getDescriptionResId()?.let {
                    Text(
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .alpha(0.85f),
                        text = stringResource(it),
                        color = White,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                GameButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.great),
                    onClick = confirmClick,
                    height = 54.dp,
                    fontSize = 21.sp
                )
            }
        }
    }
}

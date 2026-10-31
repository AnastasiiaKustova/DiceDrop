package com.example.dicedrop.presentation.game.ui.popups

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
import com.example.dicedrop.domain.entity.BonusItem
import com.example.dicedrop.presentation.common.ConfettiBurst
import com.example.dicedrop.presentation.common.DialogScrim
import com.example.dicedrop.presentation.common.GameButton
import com.example.dicedrop.presentation.common.IconCircle
import com.example.dicedrop.presentation.common.consumeClicks
import com.example.dicedrop.presentation.common.spaced
import com.example.dicedrop.presentation.game.ui.bottompanel.getRewardIconResId
import com.example.dicedrop.presentation.ui.theme.ButtonText
import com.example.dicedrop.presentation.ui.theme.DarkBlue
import com.example.dicedrop.presentation.ui.theme.Green
import com.example.dicedrop.presentation.ui.theme.LightBlue
import com.example.dicedrop.presentation.ui.theme.MidBlue
import com.example.dicedrop.presentation.ui.theme.Orange
import com.example.dicedrop.presentation.ui.theme.White
import com.example.dicedrop.presentation.ui.theme.Yellow

@Composable
fun NewRecordDialog(
    record: Int,
    previousRecord: Int,
    rewardsList: List<BonusItem>,
    confirmClick: () -> Unit,
    // Промо-ролик подписывает кнопку по-своему
    buttonText: String = stringResource(R.string.continue_game)
) {
    // Окно "выпрыгивает" с лёгкой пружиной
    val appear = remember { Animatable(0.3f) }
    LaunchedEffect(Unit) {
        appear.animateTo(
            1f,
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
    }

    // Три награды в ряд не помещаются в размере макета, поэтому круги чуть меньше
    val many = rewardsList.size > 2
    val circle = if (many) 70.dp else 84.dp
    val icon = if (many) 50.dp else 60.dp

    DialogScrim {
        Box(
            modifier = Modifier
                .padding(horizontal = 28.dp, vertical = 16.dp)
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = appear.value
                    scaleY = appear.value
                }
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 54.dp)
                    .fillMaxWidth()
                    .background(DarkBlue, RoundedCornerShape(30.dp))
                    .consumeClicks()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 66.dp, bottom = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.new_record),
                        color = Yellow,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = record.spaced(),
                        color = White,
                        fontSize = 58.sp,
                        lineHeight = 62.sp,
                        fontWeight = FontWeight.Black
                    )
                    if (previousRecord > 0) {
                        Text(
                            modifier = Modifier.alpha(0.8f),
                            text = stringResource(R.string.previous_record, previousRecord.spaced()),
                            color = White,
                            fontSize = 14.sp
                        )
                    }

                    if (rewardsList.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            modifier = Modifier.alpha(0.85f),
                            text = stringResource(R.string.rewards_caps),
                            color = White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.padding(bottom = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(if (many) 22.dp else 34.dp)
                        ) {
                            rewardsList.forEach { reward ->
                                Box {
                                    IconCircle(
                                        iconRes = reward.type.getRewardIconResId(),
                                        size = circle,
                                        iconSize = icon,
                                        ring = 7.dp,
                                        ringColor = MidBlue
                                    )
                                    Text(
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .offset(x = 3.dp, y = 3.dp)
                                            .background(Yellow, CircleShape)
                                            .padding(horizontal = 9.dp, vertical = 2.dp),
                                        text = "+${reward.count}",
                                        color = ButtonText,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    GameButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = buttonText,
                        onClick = confirmClick,
                        height = 56.dp,
                        fontSize = 22.sp
                    )
                }
            }

            Box(modifier = Modifier.align(Alignment.TopCenter)) {
                // Праздничный залп из-за кубка
                ConfettiBurst(
                    key = Unit,
                    modifier = Modifier.matchParentSize(),
                    count = 64,
                    power = 1.15f
                )
                IconCircle(
                    iconRes = R.drawable.ic_trophy,
                    size = 108.dp,
                    iconSize = 78.dp,
                    ring = 8.dp,
                    ringColor = DarkBlue
                )
            }
        }
    }
}

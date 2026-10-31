package com.example.dicedrop.presentation.achievements.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
import com.example.dicedrop.domain.entity.achievement.Achievement
import com.example.dicedrop.presentation.achievements.animation.RewardClaim
import com.example.dicedrop.presentation.achievements.animation.RewardStage
import com.example.dicedrop.presentation.common.GameButton
import com.example.dicedrop.presentation.common.GameIcons
import com.example.dicedrop.presentation.common.IconCircle
import com.example.dicedrop.presentation.common.ProgressBar
import com.example.dicedrop.presentation.game.ui.bottompanel.getIconResId
import com.example.dicedrop.presentation.ui.theme.DeepBlue
import com.example.dicedrop.presentation.ui.theme.Green
import com.example.dicedrop.presentation.ui.theme.LightBlue
import com.example.dicedrop.presentation.ui.theme.MidBlue
import com.example.dicedrop.presentation.ui.theme.White
import com.example.dicedrop.presentation.ui.theme.Yellow
import kotlinx.coroutines.launch

val Achievement.isCompleted: Boolean get() = totalCount >= requiredCount

val Achievement.canClaim: Boolean get() = isCompleted && !isRewardClaimed

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AchievementBox(
    modifier: Modifier = Modifier,
    achievement: Achievement,
    rewardStage: RewardStage? = null,
    onGetRewardClick: (RewardClaim) -> Unit,
) {
    val claimable = achievement.canClaim
    val claimed = achievement.isRewardClaimed
    val shape = RoundedCornerShape(22.dp)
    // Положения для анимации нужны только в момент нажатия, поэтому без перерисовки
    val centers = remember { mutableMapOf<Int, Offset>() }

    val cardAlpha by animateFloatAsState(if (claimed) 0.6f else 1f, label = "cardAlpha")
    val frameAlpha by animateFloatAsState(if (claimable) 1f else 0f, label = "frameAlpha")

    // Карточка подпрыгивает и вспыхивает, когда игрок забирает награду
    val bump = remember { Animatable(1f) }
    val flash = remember { Animatable(0f) }
    val claiming = rewardStage == RewardStage.FLYING
    LaunchedEffect(claiming) {
        if (!claiming) return@LaunchedEffect
        launch {
            bump.animateTo(1.04f, tween(90))
            bump.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
        launch {
            flash.snapTo(1f)
            flash.animateTo(0f, tween(500))
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = bump.value
                scaleY = bump.value
            }
            .alpha(cardAlpha)
            // Готовая к получению карточка подсвечена жёлтой рамкой с ореолом
            .border(4.dp, Yellow.copy(alpha = 0.25f * frameAlpha), RoundedCornerShape(26.dp))
            .padding(4.dp)
            .border(2.dp, Yellow.copy(alpha = frameAlpha), shape)
            .drawWithContent {
                drawContent()
                val corner = 22.dp.toPx()
                drawRoundRect(
                    color = Yellow.copy(alpha = 0.35f * flash.value),
                    cornerRadius = CornerRadius(corner, corner)
                )
            }
            .background(MidBlue, shape)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconCircle(iconRes = achievement.iconId, size = 50.dp, iconSize = 38.dp)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(achievement.titleId),
                    color = White,
                    fontSize = 17.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    modifier = Modifier.alpha(0.85f),
                    text = stringResource(achievement.descriptionId),
                    color = White,
                    fontSize = 13.sp
                )
            }
            // Подпись прогресса в правом верхнем углу: здесь её место не зависит от наград и кнопки.
            // Отступ справа - на глаз, чтобы подпись выглядела вровень со скругленным краем кнопки
            Text(
                modifier = Modifier
                    .align(Alignment.Top)
                    .padding(end = 4.dp)
                    .alpha(0.85f),
                text = "${achievement.totalCount.coerceAtMost(achievement.requiredCount)} / ${achievement.requiredCount}",
                color = White,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1
            )
        }

        // У выполненного достижения полоса не нужна: о нём и так говорят рамка, кнопка или «Получено»
        if (!achievement.isCompleted) {
            ProgressBar(
                fraction = achievement.totalCount.toFloat() / achievement.requiredCount.coerceAtLeast(1),
                color = LightBlue
            )
        }

        // Нижний ряд: награды слева, кнопка справа
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Большая награда переносится на вторую строку и не наезжает на кнопку
            FlowRow(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                RewardChip(
                    modifier = Modifier.onGloballyPositioned {
                        centers[R.drawable.coin_star] = it.boundsInRoot().center
                    },
                    iconRes = R.drawable.coin_star,
                    amount = achievement.reward
                )
                achievement.additionalRewards.forEach {
                    val iconRes = it.bonusType.getIconResId()
                    RewardChip(
                        modifier = Modifier.onGloballyPositioned { chip ->
                            centers[iconRes] = chip.boundsInRoot().center
                        },
                        iconRes = iconRes,
                        amount = it.amount,
                        iconStart = 8.dp
                    )
                }
            }
            when {
                claimable -> GameButton(
                    modifier = Modifier.onGloballyPositioned {
                        centers[R.string.claim] = it.boundsInRoot().center
                    },
                    text = stringResource(R.string.claim),
                    onClick = {
                        onGetRewardClick(
                            RewardClaim(
                                achievementId = achievement.id,
                                coins = achievement.reward,
                                buttonCenter = centers[R.string.claim] ?: Offset.Zero,
                                coinsFrom = centers[R.drawable.coin_star] ?: Offset.Zero,
                                bonuses = achievement.additionalRewards.map {
                                    val iconRes = it.bonusType.getIconResId()
                                    iconRes to (centers[iconRes] ?: Offset.Zero)
                                }
                            )
                        )
                    },
                    height = 34.dp,
                    corner = 12.dp,
                    fontSize = 16.sp
                )

                claimed -> {
                    // Галочка выскакивает, только когда награду забрали на глазах у игрока
                    val pop = remember { Animatable(if (rewardStage == RewardStage.DONE) 0f else 1f) }
                    LaunchedEffect(Unit) {
                        pop.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                    }
                    Row(
                        // Вровень с подписью прогресса над ним; высота как у кнопки, чтобы карточка не прыгала
                        modifier = Modifier
                            .height(34.dp)
                            .padding(end = 4.dp)
                            .graphicsLayer {
                                scaleX = pop.value
                                scaleY = pop.value
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = GameIcons.Check,
                            contentDescription = null,
                            tint = Green,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = stringResource(R.string.claimed),
                            color = Green,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

// iconStart: круглая монета вписывается в скругление фишки, а квадратной иконке бонуса нужен отступ побольше
@Composable
private fun RewardChip(iconRes: Int, amount: Int, modifier: Modifier = Modifier, iconStart: Dp = 3.dp) {
    Row(
        modifier = modifier
            .height(30.dp)
            .background(DeepBlue, CircleShape)
            .padding(start = iconStart, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = "+$amount",
            color = White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black
        )
    }
}

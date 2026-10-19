package com.example.dicedrop.presentation.game.ui.header

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.offset
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import com.example.dicedrop.presentation.ui.theme.Navy
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
import com.example.dicedrop.presentation.common.CoinChip
import com.example.dicedrop.presentation.common.GameIcons
import com.example.dicedrop.presentation.common.RoundImageButton
import com.example.dicedrop.presentation.common.spaced
import com.example.dicedrop.presentation.game.ui.board.comboColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.example.dicedrop.presentation.ui.theme.DarkBlue
import com.example.dicedrop.presentation.ui.theme.DeepBlue
import com.example.dicedrop.presentation.ui.theme.MidBlue
import com.example.dicedrop.presentation.ui.theme.White
import com.example.dicedrop.presentation.ui.theme.Yellow

@Composable
fun GameHeader(
    modifier: Modifier = Modifier,
    totalScore: Int,
    bestScore: Int,
    coins: Int,
    combo: Int,
    trophyHighlighted: Boolean,
    rewardsToClaim: Int,
    onPauseClick: () -> Unit,
    onShopClick: () -> Unit,
    onAchievementsClick: () -> Unit
) {
    Row(
        modifier = modifier
            .background(DarkBlue)
            .fillMaxWidth()
            .heightIn(min = 92.dp)
            .padding(vertical = 8.dp, horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CoinChip(
            coins = coins,
            height = 38.dp,
            fontSize = 15.sp
        ) {
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(20.dp)
                    .background(White.copy(alpha = 0.3f))
            )
            // Та же витрина, что на кнопке магазина в меню: сумка в белом контуре читалась как замок
            Image(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onShopClick)
                    .padding(2.dp),
                painter = painterResource(R.drawable.ic_shop_bag),
                contentDescription = stringResource(R.string.store)
            )
        }

        // Равные распорки: счёт на одинаковом расстоянии от монет и от кнопок справа
        Spacer(modifier = Modifier.weight(1f))

        Score(
            totalScore = totalScore,
            bestScore = bestScore,
            combo = combo
        )

        Spacer(modifier = Modifier.weight(1f))

        HeaderButtons(
            trophyHighlighted = trophyHighlighted,
            rewardsToClaim = rewardsToClaim,
            onPauseClick = onPauseClick,
            onAchievementsClick = onAchievementsClick
        )
    }
}

// Альбомная ориентация: монеты на всю ширину левой панели, витрина магазина по центру своей части
@Composable
fun SideCoins(
    coins: Int,
    onShopClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(DeepBlue, CircleShape)
            .padding(start = 5.dp, end = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.coin_star),
            contentDescription = null,
            modifier = Modifier.size(38.dp)
        )
        Text(
            text = coins.spaced(),
            color = White,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1
        )
        Box(
            modifier = Modifier
                .padding(start = 6.dp)
                .width(1.dp)
                .height(30.dp)
                .background(White.copy(alpha = 0.3f))
        )
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            Image(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onShopClick)
                    .padding(3.dp),
                painter = painterResource(R.drawable.ic_shop_bag),
                contentDescription = stringResource(R.string.store)
            )
        }
    }
}

// Альбомная ориентация: счёт, комбо и рекорд одной карточкой
@Composable
fun SideScore(
    totalScore: Int,
    bestScore: Int,
    combo: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MidBlue, RoundedCornerShape(24.dp))
            .padding(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.alpha(0.8f),
            text = stringResource(R.string.score_caps),
            color = White,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp
        )
        Text(
            text = totalScore.spaced(),
            color = White,
            fontSize = 50.sp,
            lineHeight = 54.sp,
            fontWeight = FontWeight.Black,
            maxLines = 1
        )
        ComboBadge(combo = combo, fontSize = 14.sp, height = 22.dp)
        Box(
            modifier = Modifier
                .padding(top = 14.dp, bottom = 10.dp)
                .fillMaxWidth()
                .height(1.dp)
                .background(White.copy(alpha = 0.2f))
        )
        Text(
            modifier = Modifier.alpha(0.8f),
            text = stringResource(R.string.record_caps),
            color = White,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp
        )
        Text(
            text = maxOf(bestScore, totalScore).spaced(),
            color = White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            maxLines = 1
        )
    }
}

@Composable
private fun Score(
    totalScore: Int,
    bestScore: Int,
    combo: Int
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            modifier = Modifier.alpha(0.8f),
            text = stringResource(R.string.record_with_value, maxOf(bestScore, totalScore).spaced()),
            color = White,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp,
            maxLines = 1
        )
        Text(
            text = totalScore.spaced(),
            color = White,
            fontSize = 34.sp,
            lineHeight = 36.sp,
            fontWeight = FontWeight.Black,
            maxLines = 1
        )
        ComboBadge(combo = combo)
    }
}

// Плашка текущего комбо, видна от x2; при росте пульсирует, при сбросе плавно исчезает
@Composable
private fun ComboBadge(combo: Int, fontSize: TextUnit = 11.sp, height: Dp = 16.dp) {
    // Последнее показанное значение, чтобы при исчезновении не мелькало "x0"
    var shownCombo by remember { mutableIntStateOf(combo) }
    val pulse = remember { Animatable(1f) }

    LaunchedEffect(combo) {
        if (combo >= 2) {
            shownCombo = combo
            pulse.snapTo(1.3f)
            pulse.animateTo(1f, spring(dampingRatio = 0.4f))
        }
    }

    // Место под плашку занято всегда, чтобы счёт не прыгал, когда комбо появляется и пропадает
    Box(
        modifier = Modifier
            .padding(top = 1.dp)
            .height(height),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = combo >= 2,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Text(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = pulse.value
                        scaleY = pulse.value
                    }
                    .background(comboColor(shownCombo), RoundedCornerShape(7.dp))
                    .padding(horizontal = 7.dp),
                text = stringResource(R.string.combo_badge, shownCombo),
                color = androidx.compose.ui.graphics.Color.White,
                fontSize = fontSize,
                lineHeight = with(LocalDensity.current) { height.toSp() },
                fontWeight = FontWeight.Black,
                maxLines = 1
            )
        }
    }
}

@Composable
fun HeaderButtons(
    trophyHighlighted: Boolean,
    rewardsToClaim: Int,
    onPauseClick: () -> Unit,
    onAchievementsClick: () -> Unit,
    buttonSize: Dp = 38.dp
) {
    // Пока висит баннер достижения, кубок светится в полную силу - подсказка, куда идти за наградой
    val bannerGlow by animateFloatAsState(
        targetValue = if (trophyHighlighted) 1f else 0f,
        animationSpec = tween(300),
        label = "trophyGlow"
    )
    // Пока есть незабранные награды, кубок мягко пульсирует
    val pulse = if (rewardsToClaim > 0) trophyPulse() else 0f
    val glow = maxOf(bannerGlow, pulse)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box {
            RoundImageButton(
                modifier = Modifier.drawBehind {
                    if (glow > 0f) {
                        drawCircle(Yellow.copy(alpha = 0.55f * glow), radius = size.minDimension / 2 + 5.dp.toPx())
                    }
                },
                iconRes = R.drawable.btn_trophy,
                contentDescription = stringResource(R.string.achievements),
                size = buttonSize,
                onClick = onAchievementsClick
            )
            if (rewardsToClaim > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 3.dp, y = (-3).dp)
                        .size(18.dp)
                        .background(Yellow, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (rewardsToClaim > 9) "9+" else rewardsToClaim.toString(),
                        color = Navy,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
        RoundImageButton(
            iconRes = R.drawable.btn_pause,
            contentDescription = stringResource(R.string.pause_title),
            size = buttonSize,
            onClick = onPauseClick
        )
    }
}

// Плавная пульсация свечения кубка
@Composable
private fun trophyPulse(): Float {
    val pulse by rememberInfiniteTransition(label = "trophyPulse").animateFloat(
        initialValue = 0.25f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "trophyPulseValue"
    )
    return pulse
}

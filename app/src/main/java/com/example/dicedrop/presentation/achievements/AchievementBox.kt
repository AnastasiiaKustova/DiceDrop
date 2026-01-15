package com.example.dicedrop.presentation.achievements

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
import com.example.dicedrop.presentation.animations.AnimatedButton
import com.example.dicedrop.presentation.animations.shineEffect
import com.example.dicedrop.presentation.ui.theme.Blue
import com.example.dicedrop.presentation.ui.theme.DarkBlue

@Composable
fun AchievementBox(
    modifier: Modifier = Modifier,
    achievementID: String,
    title: String,
    description: String,
    currentProgress: Int,
    totalProgress: Int,
    reward: Int,
    isClaimed: Boolean,
    onGetRewardClick: (achievementID: String, Offset) -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        Box(
            modifier = Modifier
                .background(Color.White)
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .align(alignment = Alignment.BottomCenter),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AchievementTitle(
                    title = title,
                    description = description,
                ) {

                    if (!isClaimed) {
                        if (currentProgress >= totalProgress)
                            AnimatedButton(
                                text = "Claim",
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { cardCenter -> onGetRewardClick(achievementID, cardCenter) }
                            )
                        else {
                            ContextWithProgress(
                                current = currentProgress,
                                total = totalProgress
                            )
                        }
                    }
                }

                AnimatedRewardTransition(
                    reward = reward,
                    isClaimed = isClaimed
                )
            }
        }
    }
}

@Composable
fun AnimatedRewardTransition(
    reward: Int,
    isClaimed: Boolean
) {
    // Плавная анимация исчезновения иконки
    val iconAlpha by animateFloatAsState(
        targetValue = if (isClaimed) 0f else 1f,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        )
    )

    // Плавная анимация появления текста Completed
    val completedAlpha by animateFloatAsState(
        targetValue = if (isClaimed) 1f else 0f,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        )
    )

    Box(
        contentAlignment = Alignment.TopEnd
    ) {
        Row {
            // Иконка награды с alpha
            IconWithCoinsCount(
                reward = reward,
                modifier = Modifier.alpha(iconAlpha)
            )
        }


        // Текст Completed с alpha
        Text(
            text = "Completed",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = DarkBlue,
            modifier = Modifier.alpha(completedAlpha)
        )
    }

}

@Composable
fun RowScope.AchievementTitle(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    achievementContext: @Composable() (ColumnScope.() -> Unit),
) {
    Column(
        modifier = modifier
            .weight(1f)
            .padding(3.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            color = DarkBlue,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            text = title,
        )
        Text(
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            text = description,
        )
        achievementContext()
    }
}

@Composable
fun RowScope.IconWithCoinsCount(
    reward: Int,
    icCoinsId: Int = R.drawable.ic_coins,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .align(Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Image(
            painter = painterResource(id = icCoinsId),
            contentDescription = "",
            modifier = Modifier
                .width(50.dp)

        )
        Text(
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            text = "$reward"
        )
    }
}

@Composable
fun ContextWithProgress(
    modifier: Modifier = Modifier,
    current: Int,           // текущее значение
    total: Int              // максимум
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(30.dp),
        contentAlignment = Alignment.Center
    ) {

        LinearProgressIndicator(
            progress = { current.toFloat() / total },
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .clip(RoundedCornerShape(8.dp)),
            color = DarkBlue, // цвет заполненной части
            trackColor = Blue  // фон линии
        )

        Text(
            text = "$current / $total",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

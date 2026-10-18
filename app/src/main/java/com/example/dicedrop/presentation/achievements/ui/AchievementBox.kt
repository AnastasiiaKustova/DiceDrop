package com.example.dicedrop.presentation.achievements.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
import com.example.dicedrop.domain.entity.achievement.Achievement
import com.example.dicedrop.domain.entity.achievement.AchievementType
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
import com.example.dicedrop.domain.entity.BonusType

val Achievement.isCompleted: Boolean get() = totalCount >= requiredCount

val Achievement.canClaim: Boolean get() = isCompleted && !isRewardClaimed

fun AchievementType.getIconResId(): Int = when (this) {
    AchievementType.ROLL_TIMES -> R.drawable.die_roll_123
    AchievementType.CLEAR_ROWS,
    AchievementType.LINES_AT_ONCE -> R.drawable.ic_lines
    AchievementType.BIG_BLOCK,
    AchievementType.BOARD_CLEARED -> R.drawable.ic_board
    AchievementType.GAME_SCORE -> R.drawable.ic_score
    AchievementType.GAMES_PLAYED -> R.drawable.ic_trophy
    AchievementType.USE_ROTATE -> BonusType.ROTATE.getIconResId()
    AchievementType.USE_FREEZE -> BonusType.FREEZE.getIconResId()
    AchievementType.USE_LUCKY -> BonusType.LUCKY.getIconResId()
    AchievementType.USE_DRAFT -> BonusType.DRAFT.getIconResId()
    AchievementType.COMBO -> R.drawable.ic_combo
}

@Composable
fun AchievementBox(
    modifier: Modifier = Modifier,
    achievement: Achievement,
    onGetRewardClick: (achievementID: String, Offset) -> Unit,
) {
    val claimable = achievement.canClaim
    val claimed = achievement.isRewardClaimed
    val shape = RoundedCornerShape(22.dp)
    var buttonCenter by remember { mutableStateOf(Offset.Zero) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (claimed) 0.6f else 1f)
            .then(
                // Готовая к получению карточка подсвечена жёлтой рамкой с ореолом
                if (claimable) Modifier
                    .border(4.dp, Yellow.copy(alpha = 0.25f), RoundedCornerShape(26.dp))
                    .padding(4.dp)
                    .border(2.dp, Yellow, shape)
                else Modifier.padding(4.dp)
            )
            .background(MidBlue, shape)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconCircle(iconRes = achievement.type.getIconResId(), size = 50.dp, iconSize = 38.dp)
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
        }

        ProgressBar(
            fraction = achievement.totalCount.toFloat() / achievement.requiredCount.coerceAtLeast(1),
            color = when {
                claimed -> Green
                claimable -> Yellow
                else -> LightBlue
            }
        )

        // Нижний ряд: награды слева, кнопка справа, а подпись прогресса - по центру под полосой,
        // в том же ряду, чтобы не занимать отдельную строку
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
                modifier = Modifier
                    .align(Alignment.Center)
                    .alpha(0.85f),
                text = "${achievement.totalCount.coerceAtMost(achievement.requiredCount)} / ${achievement.requiredCount}",
                color = White,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    RewardChip(iconRes = R.drawable.coin_star, amount = achievement.reward)
                    achievement.additionalRewards.forEach {
                        RewardChip(iconRes = it.bonusType.getIconResId(), amount = it.amount)
                    }
                }
                when {
                    claimable -> GameButton(
                        modifier = Modifier.onGloballyPositioned {
                            buttonCenter = it.boundsInRoot().center
                        },
                        text = stringResource(R.string.claim),
                        onClick = { onGetRewardClick(achievement.id, buttonCenter) },
                        height = 34.dp,
                        corner = 12.dp,
                        fontSize = 16.sp
                    )

                    claimed -> Row(
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

@Composable
private fun RewardChip(iconRes: Int, amount: Int) {
    Row(
        modifier = Modifier
            .height(30.dp)
            .background(DeepBlue, CircleShape)
            .padding(start = 3.dp, end = 10.dp),
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

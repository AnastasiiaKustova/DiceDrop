package com.example.dicedrop.presentation.game.ui.popups

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
import com.example.dicedrop.presentation.common.DialogScrim
import com.example.dicedrop.presentation.common.GameButton
import com.example.dicedrop.presentation.common.GameIcons
import com.example.dicedrop.presentation.common.consumeClicks
import com.example.dicedrop.presentation.common.drawDie
import com.example.dicedrop.presentation.common.spaced
import com.example.dicedrop.presentation.ui.theme.ButtonText
import com.example.dicedrop.presentation.ui.theme.DarkBlue
import com.example.dicedrop.presentation.ui.theme.DeepBlue
import com.example.dicedrop.presentation.ui.theme.MidBlue
import com.example.dicedrop.presentation.ui.theme.Navy
import com.example.dicedrop.presentation.ui.theme.White

@Composable
fun GameOverDialog(
    gameScore: Int,
    bestScore: Int,
    canUndo: Boolean,
    coinsEarned: Int,
    onDoubleCoinsClick: () -> Unit,
    onRestartClick: () -> Unit,
    onWatchAdsClick: () -> Unit
) {
    DialogScrim {
        Box(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 16.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 50.dp)
                    .fillMaxWidth()
                    .background(DarkBlue, RoundedCornerShape(30.dp))
                    .consumeClicks()
                    .padding(start = 20.dp, end = 20.dp, top = 62.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.game_over),
                    color = White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    modifier = Modifier.alpha(0.8f),
                    text = stringResource(R.string.game_over_subtitle),
                    color = White,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ScorePanel(
                        modifier = Modifier.weight(1f),
                        title = stringResource(R.string.score_caps),
                        value = gameScore
                    )
                    ScorePanel(
                        modifier = Modifier.weight(1f),
                        title = stringResource(R.string.record_caps),
                        value = maxOf(bestScore, gameScore)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                CoinsEarnedPanel(
                    coinsEarned = coinsEarned,
                    onDoubleCoinsClick = onDoubleCoinsClick
                )

                // Отмена хода возможна, только если есть сохранённое состояние до последнего хода
                if (canUndo) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DeepBlue, RoundedCornerShape(22.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .background(White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = GameIcons.Undo,
                                    contentDescription = null,
                                    tint = ButtonText,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = stringResource(R.string.try_again),
                                    color = White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    modifier = Modifier.alpha(0.85f),
                                    text = stringResource(R.string.undo_for_ad),
                                    color = White,
                                    fontSize = 13.sp
                                )
                            }
                        }
                        GameButton(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(R.string.watch_ad),
                            onClick = onWatchAdsClick,
                            height = 50.dp,
                            corner = 16.dp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                GameButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.restart_game),
                    onClick = onRestartClick,
                    color = MidBlue,
                    contentColor = White,
                    icon = GameIcons.Restart
                )
            }

            SadDie(modifier = Modifier.align(Alignment.TopCenter))
        }
    }
}

@Composable
private fun ScorePanel(
    modifier: Modifier = Modifier,
    title: String,
    value: Int
) {
    Column(
        modifier = modifier
            .background(MidBlue, RoundedCornerShape(18.dp))
            .padding(top = 8.dp, bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.alpha(0.85f),
            text = title,
            color = White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.5.sp
        )
        Text(
            text = value.spaced(),
            color = White,
            fontSize = 30.sp,
            lineHeight = 33.sp,
            fontWeight = FontWeight.Black,
            maxLines = 1
        )
    }
}

// Монеты за партию и кнопка удвоить их за рекламу. Удвоение сразу начинает новую игру,
// иначе можно было бы удвоить, а потом отменить ход и играть дальше
@Composable
private fun CoinsEarnedPanel(
    coinsEarned: Int,
    onDoubleCoinsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DeepBlue, RoundedCornerShape(18.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.coin_star),
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )
            Column {
                Text(
                    modifier = Modifier.alpha(0.85f),
                    text = stringResource(R.string.coins_earned),
                    color = White,
                    fontSize = 13.sp
                )
                Text(
                    text = "+$coinsEarned",
                    color = White,
                    fontSize = 22.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        if (coinsEarned > 0) {
            Text(
                modifier = Modifier.alpha(0.85f),
                text = stringResource(R.string.double_coins_hint),
                color = White,
                fontSize = 13.sp
            )
            GameButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.double_and_restart),
                onClick = onDoubleCoinsClick,
                height = 46.dp,
                corner = 14.dp,
                fontSize = 16.sp
            )
        }
    }
}

// Грустный кубик: глаза-крестики и опущенный рот
@Composable
private fun SadDie(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(116.dp)
            .background(DarkBlue, CircleShape)
            .padding(8.dp)
            .background(White, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(56.dp)
                .rotate(-12f)
        ) {
            drawDie(value = 0)
            val u = size.width / 56f
            val stroke = 3.2f * u
            fun line(x1: Float, y1: Float, x2: Float, y2: Float) = drawLine(
                color = Navy,
                start = Offset(x1 * u, y1 * u),
                end = Offset(x2 * u, y2 * u),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )
            line(10f, 12f, 18f, 20f)
            line(18f, 12f, 10f, 20f)
            line(32f, 12f, 40f, 20f)
            line(40f, 12f, 32f, 20f)
            drawArc(
                color = Navy,
                startAngle = 200f,
                sweepAngle = 140f,
                useCenter = false,
                topLeft = Offset(17f * u, 32f * u),
                size = androidx.compose.ui.geometry.Size(16f * u, 12f * u),
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }
    }
}

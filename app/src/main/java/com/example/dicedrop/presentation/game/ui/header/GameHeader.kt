package com.example.dicedrop.presentation.game.ui.header

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
import com.example.dicedrop.presentation.game.ui.board.comboColor
import com.example.dicedrop.presentation.ui.theme.BlueGradientDark
import com.example.dicedrop.presentation.ui.theme.BlueGradientLight
import com.example.dicedrop.presentation.ui.theme.White

@Composable
fun GameHeader(
    modifier: Modifier = Modifier,
    totalScore: Int,
    bestScore: Int,
    coins: Int,
    combo: Int,
    isLandscape: Boolean,
    onPauseClick: () -> Unit,
    onShopClick: () -> Unit
) {

    Row(
        modifier = modifier
            .background(
                Brush.linearGradient(
                    colors = listOf(BlueGradientLight, BlueGradientDark),
                    start = Offset(0f, 0f),
                    end = Offset(0f, Float.POSITIVE_INFINITY)
                )
            )
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center
    ) {

        CoinsCounter(
            coins = coins
        )
        Score(
            totalScore = totalScore,
            bestScore = bestScore,
            combo = combo,
            isLandscape = isLandscape,
        )
        Shop(
            onPauseClick = onPauseClick,
            onClick = onShopClick
        )
    }
}

@Composable
private fun RowScope.CoinsCounter(
    modifier: Modifier = Modifier,
    coins: Int,
){
    Row(
        modifier = modifier.weight(1f).padding(bottom = 5.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            painter = painterResource(id = R.drawable.coin),
            contentDescription = "",
            modifier = Modifier
                .width(27.dp)

        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            color = White,
            text = "$coins",
            fontSize = if (coins >= 1000) 15.sp else 22.sp,
            maxLines = 1
        )
    }
}

@Composable
private fun RowScope.Score(
    modifier: Modifier = Modifier,
    totalScore: Int,
    bestScore: Int,
    combo: Int,
    isLandscape: Boolean,
){

    if (isLandscape){
        Row(
            modifier = modifier
                .weight(2f).padding(bottom = 5.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                color = White,
                text = stringResource(R.string.best_score_short) + bestScore,
                fontSize = 16.sp
            )

            Text(
                color = White,
                text = stringResource(R.string.total_score_short) + totalScore,
                fontSize = 16.sp
            )

            ComboBadge(combo = combo)
        }
        
    }
    else{
        Column(
            modifier = modifier
                .weight(2f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .offset(y = 5.dp),
                color = White,
                text = stringResource(R.string.best_score_short) + bestScore,
                fontSize = 12.sp
            )

            Row(
                modifier = Modifier
                    .height(38.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "$totalScore",
                    color = Color.White,
                    fontSize = if (isLandscape) 12.sp else 36.sp,
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(includeFontPadding = false),
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Bottom,
                            trim = LineHeightStyle.Trim.Both
                        )
                    )
                )

                ComboBadge(combo = combo)
            }
        }
    }


}

// Плашка текущего комбо, видна от x2; при росте пульсирует, при сбросе плавно исчезает
@Composable
private fun ComboBadge(combo: Int) {
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
                .background(comboColor(shownCombo), RoundedCornerShape(8.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp),
            text = "x$shownCombo",
            color = White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
private fun RowScope.Shop(
    modifier: Modifier = Modifier,
    onPauseClick: () -> Unit,
    onClick: () -> Unit
){
    Row(
        modifier = modifier
            .weight(1f),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End)
    ) {
        Button(
            modifier = Modifier.size(40.dp),
            onClick = { onPauseClick() },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(0.dp),
            shape = RectangleShape
        ) {
            Image(
                modifier = Modifier
                    .width(27.dp),
                painter = painterResource(id = R.drawable.pause),
                contentDescription = stringResource(R.string.pause_title),
            )
        }

        Button(
            modifier = Modifier.size(40.dp),
            onClick = { onClick() },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(0.dp),
            shape = RectangleShape
        ) {
            Image(
                modifier = Modifier
                    .width(27.dp),
                painter = painterResource(id = R.drawable.ic_shop),
                contentDescription = "",
            )
        }
    }
}
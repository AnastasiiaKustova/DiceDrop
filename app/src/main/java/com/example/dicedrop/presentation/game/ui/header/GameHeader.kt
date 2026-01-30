package com.example.dicedrop.presentation.game.ui.header

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
import com.example.dicedrop.presentation.ui.theme.BlueGradientDark
import com.example.dicedrop.presentation.ui.theme.BlueGradientLight
import com.example.dicedrop.presentation.ui.theme.White

@Composable
fun GameHeader(
    modifier: Modifier = Modifier,
    totalScore: Int,
    bestScore: Int,
    coins: Int,
    isLandscape: Boolean,
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
            isLandscape = isLandscape,
        )
        Shop(
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
            fontSize = 22.sp
        )
    }
}

@Composable
private fun RowScope.Score(
    modifier: Modifier = Modifier,
    totalScore: Int,
    bestScore: Int,
    isLandscape: Boolean,
){

    if (isLandscape){
        Row(
            modifier = modifier
                .weight(2f).padding(bottom = 5.dp),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            Text(
                color = White,
                text = stringResource(R.string.best_score_short) + bestScore,
                fontSize = 16.sp
            )

            Text(
                color = White,
                text = "TOTAL: " + totalScore,
                fontSize = 16.sp
            )
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

            Box(
                modifier = Modifier
                    .height(38.dp),
                contentAlignment = Alignment.Center
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
            }
        } 
    }
    
    
}

@Composable
private fun RowScope.Shop(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
){
    Box(
        modifier = modifier
            .weight(1f),
        contentAlignment = Alignment.BottomEnd
    ) {
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
package com.example.dicedrop.presentation.game.ui.popups

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
import com.example.dicedrop.presentation.animations.AnimatedButton
import com.example.dicedrop.presentation.ui.theme.DarkBlue
import com.example.dicedrop.presentation.ui.theme.TranslucentBlack
import com.example.dicedrop.presentation.ui.theme.White
import com.example.dicedrop.presentation.ui.theme.WhiteBlue

@Composable
fun GameOverDialog(
    modifier: Modifier = Modifier,
    gameScore: Int,
    rescueChance: Int,
    canUndo: Boolean,
    isLandscape: Boolean,
    onRestartClick: () -> Unit,
    onWatchAdsClick: () -> Unit
) {
    val spaceHeightModifier = if (isLandscape) Modifier.height(8.dp) else Modifier.height(16.dp)
    val boxMaxWidth = if (isLandscape) 0.45f  else 0.85f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TranslucentBlack)
            .wrapContentSize(Alignment.Center)
            .clickable { }
    ) {
        Box(
            modifier = modifier
                .padding(vertical = 20.dp)
                .wrapContentSize()
                .fillMaxWidth(boxMaxWidth)
                .background(White, RoundedCornerShape(20.dp))
                .border(
                    width = 5.dp,
                    color = DarkBlue,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(8.dp)
                .padding(bottom = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {

                Spacer(modifier = Modifier.height(20.dp))

                Image(
                    modifier = if (isLandscape) Modifier.fillMaxHeight(0.4f) else Modifier.fillMaxWidth(0.6f),
                    painter = painterResource(id = R.drawable.pic_game_over),
                    contentDescription = null
                )

                Spacer(modifier = spaceHeightModifier)

                Text(
                    text = stringResource(R.string.game_over),
                    fontWeight = FontWeight.Bold,
                    color = DarkBlue,
                    fontSize = if (isLandscape) 20.sp else 25.sp
                )

                Text(
                    text = stringResource(R.string.score_with_count, gameScore),
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    fontSize = if (isLandscape) 12.sp else 16.sp
                )

                if (isLandscape) Spacer(modifier = Modifier.height(20.dp)) else Spacer(modifier = Modifier.height(40.dp))

                RerollLabel(
                    isLandscape = isLandscape,
                    text = if (canUndo) stringResource(R.string.watch_an_ad_to_undo_move)
                        else stringResource(R.string.watch_an_ad_to_reroll_the_dice),
                    chanceText = if (canUndo) null else stringResource(R.string.rescue_chance, rescueChance),
                    onClick = {onWatchAdsClick()}
                )

                Spacer(modifier = spaceHeightModifier)

                AnimatedButton(
                    text = stringResource(R.string.restart),
                    modifier = Modifier.wrapContentHeight(),
                    onClick = { _ -> onRestartClick() }
                )
            }
        }
    }
}

@Composable
private fun RerollLabel(
    modifier: Modifier = Modifier,
    isLandscape: Boolean,
    text: String,
    chanceText: String?,
    onClick: () -> Unit
){
    val boxHeight = if (isLandscape) 40.dp else 80.dp
    val boxMaxWidth = if (isLandscape) 0.8f else 0.9f
    Box(
        modifier = modifier
            .height(boxHeight)
            .fillMaxWidth(boxMaxWidth)
            .clickable { onClick() }
        ,
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(WhiteBlue, RoundedCornerShape(8.dp))
                .padding(8.dp)
                .padding(horizontal = 8.dp)
            ,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                modifier = Modifier.fillMaxHeight(1f),
                painter = painterResource(id = R.drawable.ic_update_ad),
                contentDescription = null
            )
            Text(
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 1.5.em,
                text = text,
                color = Color.Black
            )
            if (chanceText != null) {
                Text(
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    text = chanceText,
                    color = Color.Black
                )
            }
        }
        Box(
            modifier = Modifier
                .offset(y = -16.dp)
                .align(Alignment.TopCenter)
                .background(WhiteBlue, RoundedCornerShape(8.dp))
                .padding(top = 4.dp)
                .padding(horizontal = 12.dp)

        ) { Text(
            text = stringResource(R.string.try_again),
            color = DarkBlue
        ) }
    }
}
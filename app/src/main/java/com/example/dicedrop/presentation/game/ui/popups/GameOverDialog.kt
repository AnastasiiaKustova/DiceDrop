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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
import com.example.dicedrop.presentation.animations.AnimatedButton
import com.example.dicedrop.presentation.ui.theme.DarkBlue
import com.example.dicedrop.presentation.ui.theme.White
import com.example.dicedrop.presentation.ui.theme.WhiteBlue

@Composable
fun GameOverDialog(
    modifier: Modifier = Modifier,
    gameScore: Int,
    onRestartClick: () -> Unit,
    onWatchAdsClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x88000000)) // полупрозрачный черный фон
            .wrapContentSize(Alignment.Center)
    ) {
        Box(
            modifier = modifier
                .wrapContentSize()
                .fillMaxWidth(0.85f)
                .fillMaxHeight(0.7f)
                .background(White, RoundedCornerShape(20.dp))
                .border(
                    width = 5.dp,
                    color = DarkBlue,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {

                Spacer(modifier = Modifier.height(20.dp))

                Image(
                    modifier = Modifier.fillMaxWidth(0.6f),
                    painter = painterResource(id = R.drawable.pic_game_over),
                    contentDescription = null
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "ИГРА ОКОНЧЕНА",
                    color = DarkBlue,
                    fontSize = 25.sp
                )

                Text(
                    text = "Счет $gameScore",
                    color = DarkBlue,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(40.dp))

                rerollLabel( onClick = {onWatchAdsClick()})

                Spacer(modifier = Modifier.height(40.dp))

                AnimatedButton(
                    text = "ПЕРЕЗАПУСТИТЬ",
                    modifier = Modifier.wrapContentHeight(),
                    onClick = { _ -> onRestartClick() }
                )
            }
        }
    }
}

@Composable
private fun rerollLabel(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
){
    Box(
        modifier = modifier
            .height(80.dp)
            .fillMaxWidth(0.85f)
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
                .padding(vertical = 8.dp)
            ,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                modifier = Modifier.width(40.dp),
                painter = painterResource(id = R.drawable.ic_update),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Перебросить кубики",
                color = DarkBlue
            )
        }
        Box(
            modifier = Modifier
                .offset(y = -16.dp)
                .align(Alignment.TopCenter)
                .background(WhiteBlue, RoundedCornerShape(8.dp))
                .padding(4.dp)
                .padding(horizontal = 8.dp)

        ) { Text(
            text = "Еще раз",
            color = DarkBlue
        ) }
    }
}
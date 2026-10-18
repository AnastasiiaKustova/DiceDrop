package com.example.dicedrop.presentation.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
import com.example.dicedrop.presentation.ui.theme.DarkBlue
import com.example.dicedrop.presentation.ui.theme.TranslucentBlack
import com.example.dicedrop.presentation.ui.theme.White
import com.example.dicedrop.presentation.ui.theme.WhiteBlue

@Composable
fun DrawPause(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onRestartClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TranslucentBlack)
            // Пауза лежит поверх игры: не пропускаем касания к полю под ней
            .pointerInput(Unit) {}
            .wrapContentSize(Alignment.Center)
            .padding(20.dp)
    ) {
        Box(
            modifier = modifier
                .wrapContentSize()
                .background(White, RoundedCornerShape(20.dp))
                .border(
                    width = 5.dp,
                    color = DarkBlue,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 40.dp, vertical = 40.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    Modifier
                        .fillMaxWidth()
                ) {

                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = stringResource(R.string.pause_title),
                        fontWeight = FontWeight.Bold,
                        color = DarkBlue,
                        fontSize = 28.sp
                    )

                    IconButton(onClick = {
                        onBackClick()
                    },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                        ,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = (stringResource(R.string.back)),
                            modifier = Modifier
                                .background(WhiteBlue)
                                .size(SwitchDefaults.IconSize * 2),
                            tint = DarkBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedButton(onClick = {
                    onRestartClick()
                },
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = DarkBlue,
                        containerColor = White
                    )
                ) {
                    Text(
                        text = stringResource(R.string.restart_game)
                    )
                }
            }
        }
    }
}

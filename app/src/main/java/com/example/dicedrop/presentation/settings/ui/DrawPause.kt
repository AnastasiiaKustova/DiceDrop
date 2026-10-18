package com.example.dicedrop.presentation.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
import com.example.dicedrop.presentation.common.DialogScrim
import com.example.dicedrop.presentation.common.GameButton
import com.example.dicedrop.presentation.common.GameIcons
import com.example.dicedrop.presentation.common.consumeClicks
import com.example.dicedrop.presentation.ui.theme.DarkBlue
import com.example.dicedrop.presentation.ui.theme.MidBlue
import com.example.dicedrop.presentation.ui.theme.White

@Composable
fun DrawPause(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onRestartClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    // Пауза лежит поверх игры: касания к полю под ней не проходят
    DialogScrim {
        Column(
            modifier = modifier
                .padding(horizontal = 40.dp)
                .fillMaxWidth()
                .background(DarkBlue, RoundedCornerShape(30.dp))
                .consumeClicks()
                .padding(start = 20.dp, end = 20.dp, top = 22.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.pause_title),
                color = White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            GameButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.continue_game),
                onClick = onBackClick,
                height = 60.dp,
                corner = 20.dp,
                fontSize = 22.sp,
                icon = GameIcons.Play
            )

            GameButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.restart_game),
                onClick = onRestartClick,
                color = MidBlue,
                contentColor = White
            )

            GameButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.to_menu),
                onClick = onMenuClick,
                color = MidBlue,
                contentColor = White
            )
        }
    }
}

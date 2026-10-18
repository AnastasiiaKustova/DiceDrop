package com.example.dicedrop.presentation.game.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
import com.example.dicedrop.presentation.common.GameButton
import com.example.dicedrop.presentation.ui.theme.ButtonText
import com.example.dicedrop.presentation.ui.theme.MutedText
import com.example.dicedrop.presentation.ui.theme.Red
import com.example.dicedrop.presentation.ui.theme.White

// Кремовая плашка-уведомление. Вторая строка сообщения (после \n) - пояснение мелким шрифтом
@Composable
fun GameSnackbarHost(
    snackbarHostState: SnackbarHostState,
    bottomPadding: Dp = 80.dp,
    // По умолчанию - монета с красным "!", для остальных сообщений - своя иконка без значка
    @DrawableRes iconRes: Int = R.drawable.coin_star,
    showAlert: Boolean = true
) {
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier.padding(bottom = bottomPadding)
    ) { snackbarData ->
        val lines = snackbarData.visuals.message.split("\n", limit = 2)
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp)
                .fillMaxWidth()
                .shadow(12.dp, RoundedCornerShape(22.dp))
                .background(White, RoundedCornerShape(22.dp))
                .padding(start = 12.dp, end = 10.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(modifier = Modifier.size(42.dp)) {
                Image(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(42.dp)
                )
                if (showAlert) Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 3.dp, y = 3.dp)
                        .size(20.dp)
                        .background(Red, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "!", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lines[0],
                    color = ButtonText,
                    fontSize = 16.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Black
                )
                if (lines.size > 1) {
                    Text(
                        text = lines[1],
                        color = MutedText,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }
            }
            snackbarData.visuals.actionLabel?.let {
                GameButton(
                    text = it,
                    onClick = { snackbarData.performAction() },
                    height = 40.dp,
                    corner = 14.dp,
                    fontSize = 14.sp
                )
            }
        }
    }
}

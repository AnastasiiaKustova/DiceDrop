package com.example.dicedrop.presentation.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
import com.example.dicedrop.presentation.common.OutlinedText
import com.example.dicedrop.presentation.ui.theme.Blue
import com.example.dicedrop.presentation.ui.theme.CellBG
import com.example.dicedrop.presentation.ui.theme.DarkBlue


@Composable
fun MenuContent(component: MenuComponent) {
    Menu(
        onStartGameClick = { component.onStartGameClick() },
        onAchievementsClick = { component.onAchievementsClick() },
        onShopClick = { component.onShopClick() },
        onCloseAppClick = { component.onCloseAppClick() },
    )
}

@Composable
private fun MenuButton(
    modifier: Modifier = Modifier,
    title: String,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier
            .width(200.dp)
            .height(50.dp),
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = CellBG
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            width = 5.dp,
            brush = Brush.linearGradient(
                colors = listOf(Color.White, Blue),
                start = Offset(0f, 0f),
                end = Offset(0f, Float.POSITIVE_INFINITY)
            )
        ),
        shape = RoundedCornerShape(8.dp),
        onClick = onClick
    ) {
        Text(
            color = Color.White,
            fontSize = 20.sp,
            text = title
        )
    }
}

@Composable
private fun Menu(
    onStartGameClick: () -> Unit,
    onAchievementsClick: () -> Unit,
    onShopClick: () -> Unit,
    onCloseAppClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(CellBG, DarkBlue),
                    start = Offset(0f, 0f),
                    end = Offset(0f, Float.POSITIVE_INFINITY)
                )
            )
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        OutlinedText(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .wrapContentSize(),
            outlineColor = Color.White,
            fontSize = (0.07f * LocalConfiguration.current.screenHeightDp).sp,
            fontWeight = FontWeight.Bold,
            text = stringResource(R.string.app_name_DICE_DROP),
        )
        /*Text(
            color = Color.White,
            fontSize = (0.06f * LocalConfiguration.current.screenHeightDp).sp,
            fontWeight = FontWeight.Bold,
            text = stringResource(R.string.app_name_DROP)
        )*/

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MenuButton(
                title = stringResource(R.string.startGame),
                onClick = { onStartGameClick() }
            )

            MenuButton(
                title = stringResource(R.string.achievements),
                onClick = { onAchievementsClick() }
            )

            MenuButton(
                title = stringResource(R.string.store),
                onClick = { onShopClick() }
            )

            MenuButton(
                title = stringResource(R.string.close),
                onClick = { onCloseAppClick() }
            )
        }
    }
}

@Preview
@Composable
fun Test() {
    Menu(
        onStartGameClick = {  },
        onAchievementsClick = {  },
        onShopClick = {  },
        onCloseAppClick = {  },
    )
}
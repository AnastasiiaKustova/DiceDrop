package com.example.dicedrop.presentation.game.ui.bottompanel

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.dicedrop.presentation.ui.theme.DarkBlue
import com.example.dicedrop.presentation.ui.theme.Red
import com.example.dicedrop.presentation.ui.theme.White

@Composable
fun RowScope.BonusButton(
    modifier: Modifier = Modifier,
    dragModifier: Modifier = Modifier,
    @DrawableRes iconResId: Int,
    count: Int,
    onBonusClick: () -> Unit,
    onCounterClick: () -> Unit
) {
    Box(
        modifier = modifier
            .padding(8.dp)
            .weight(1f)           // растягиваем по доступной ширине родителя
            .aspectRatio(1f)      // сохраняем квадратную форму
            .padding(8.dp),       // отступ внутри
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onBonusClick,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = White),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.fillMaxSize()
        ) {}

        BonusButtonCounter(count, onCounterClick)

        Image(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            modifier = dragModifier.fillMaxSize().padding(8.dp)
        )
    }
}

@Composable
fun BoxScope.BonusButtonCounter(
    count: Int,
    onClick: () -> Unit
) {
    val text = if (count > 0) count.toString() else "+"
    val color = if (count > 0) White else Red
    val textColor = if (count > 0) DarkBlue else White  // доля от размера кнопки

    Box(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .offset(x = 8.dp, y = 8.dp)
            .fillMaxSize(fraction = 0.4f) // занимает 35% от родительской кнопки
            .aspectRatio(1f)
            .zIndex(2f)
            .background(color, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = (0.04f * LocalConfiguration.current.screenWidthDp).sp,
            modifier = Modifier.clickable { onClick() }
        )
    }
}
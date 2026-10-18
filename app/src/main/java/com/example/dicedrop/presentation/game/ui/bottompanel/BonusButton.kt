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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color.Companion.Gray
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
fun BonusButton(
    modifier: Modifier = Modifier,
    @DrawableRes iconResId: Int,
    count: Int,
    isLandscape: Boolean,
    enabled: Boolean = true,
    onBonusClick: () -> Unit,
    onCounterClick: () -> Unit
) {

    val modifier = if (isLandscape) modifier.fillMaxWidth(0.8f) else modifier
    Box(
        modifier = modifier
            .padding(bottom = 8.dp)
            .aspectRatio(1f),

        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onBonusClick,
            enabled = enabled,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = White),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.fillMaxSize()
        ) {}

        BonusButtonCounter(count, onCounterClick, enabled)

        Image(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().padding(8.dp).alpha(if (enabled) 1f else 0.4f)
        )
    }
}

@Composable
fun BoxScope.BonusButtonCounter(
    count: Int,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    val text = if (count >= 100) "99" else if (count > 0) count.toString() else "+"
    val color = if (!enabled) Gray else if (count > 0) White else Red
    val textColor = if (!enabled) White else if (count > 0) DarkBlue else White

    Box(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .offset(x = 8.dp, y = 8.dp)
            .fillMaxSize(fraction = 0.45f)
            .aspectRatio(1f)
            .zIndex(2f)
            .background(color, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = (0.03f * LocalConfiguration.current.screenWidthDp).sp,
            modifier = Modifier.clickable { onClick() }
        )
    }
}
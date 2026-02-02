package com.example.dicedrop.presentation.shop.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.domain.entity.BonusItem
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.presentation.common.OutlinedText
import com.example.dicedrop.presentation.game.ui.bottompanel.getIconResId
import com.example.dicedrop.presentation.ui.theme.DarkBlue
import com.example.dicedrop.presentation.ui.theme.White

@Composable
fun RandomBonusDialog(
    modifier: Modifier = Modifier,
    bonus: BonusItem,
    confirmClick: () -> Unit
) {
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        visible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x88000000))
            .clickable { confirmClick() },
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = scaleIn(
                initialScale = 0.2f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = modifier
                        .fillMaxWidth(0.55f)
                        .wrapContentHeight()
                        .background(White, RoundedCornerShape(20.dp))
                        .border(
                            width = 5.dp,
                            color = DarkBlue,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(36.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "You got",
                        color = DarkBlue,
                        fontSize = 20.sp)
                    OutlinedText(
                        text = bonus.type.toString(),
                        fillColor = DarkBlue,
                        outlineColor = DarkBlue,
                        fontSize = 28.sp
                    )
                    Spacer(Modifier.size(12.dp))
                    Image(
                        modifier = Modifier.size(125.dp),
                        painter = painterResource(id = bonus.type.getIconResId()),
                        contentDescription = null,
                    )
                }
                Text("Tap to continue", color = Color.White)
            }
        }
    }
}

@Preview
@Composable
fun Test(){
    RandomBonusDialog(
        bonus = BonusItem(
            type = BonusType.BOMB,
            count = 1
        )
    ){}
}
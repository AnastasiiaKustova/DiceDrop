package com.example.dicedrop.presentation.settings.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.domain.feedback.GameFeedback
import com.example.dicedrop.presentation.common.LocalGameFeedback
import com.example.dicedrop.presentation.ui.theme.ButtonText
import com.example.dicedrop.presentation.ui.theme.DeepBlue
import com.example.dicedrop.presentation.ui.theme.MidBlue
import com.example.dicedrop.presentation.ui.theme.White
import com.example.dicedrop.presentation.ui.theme.Yellow

// Карточка настройки с переключателем: иконка, название и тумблер; нажать можно на всю карточку
@Composable
fun ToggleSetting(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val feedback = LocalGameFeedback.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(MidBlue)
            .clickable(role = Role.Switch) {
                feedback.play(GameFeedback.Sound.BUTTON_CLICK)
                onCheckedChange(!checked)
            }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ButtonText,
                modifier = Modifier.size(26.dp)
            )
        }
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            color = White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black
        )
        GameSwitch(checked = checked)
    }
}

// Круглый переключатель с подписью для окна паузы: включено - светлый круг, выключено - тёмный и перечёркнутый
@Composable
fun RoundToggle(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val feedback = LocalGameFeedback.current
    Column(
        modifier = Modifier.alpha(if (checked) 1f else 0.7f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(if (checked) White else DeepBlue)
                .clickable(role = Role.Switch) {
                    feedback.play(GameFeedback.Sound.BUTTON_CLICK)
                    onCheckedChange(!checked)
                }
                .drawWithContent {
                    drawContent()
                    if (!checked) {
                        val inset = size.minDimension * 0.27f
                        drawLine(
                            color = White,
                            start = Offset(inset, inset),
                            end = Offset(size.width - inset, size.height - inset),
                            strokeWidth = 3.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (checked) ButtonText else White,
                modifier = Modifier.size(28.dp)
            )
        }
        Text(
            text = title,
            color = White,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

// Тумблер в цветах игры: жёлтый - включено, тёмно-синий - выключено
@Composable
private fun GameSwitch(checked: Boolean) {
    val track by animateColorAsState(if (checked) Yellow else DeepBlue, label = "switchTrack")
    val thumbOffset by animateDpAsState(if (checked) 24.dp else 0.dp, label = "switchThumb")

    Box(
        modifier = Modifier
            .size(width = 56.dp, height = 32.dp)
            .background(track, CircleShape)
            .padding(3.dp)
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(26.dp)
                .shadow(2.dp, CircleShape)
                .background(White, CircleShape)
        )
    }
}

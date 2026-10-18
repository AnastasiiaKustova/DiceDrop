package com.example.dicedrop.presentation.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.presentation.common.GameIcons
import com.example.dicedrop.presentation.ui.theme.ButtonText
import com.example.dicedrop.presentation.ui.theme.DeepBlue
import com.example.dicedrop.presentation.ui.theme.MidBlue
import com.example.dicedrop.presentation.ui.theme.White
import com.example.dicedrop.presentation.ui.theme.Yellow
import kotlin.math.roundToInt

// Карточка громкости музыки: иконка, название, процент и ползунок
@Composable
fun MusicSetting(
    title: String,
    value: Float,
    onValueChangeFinished: (Float) -> Unit
) {
    var position by rememberSaveable { mutableFloatStateOf(value.coerceIn(0f, 100f)) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MidBlue, RoundedCornerShape(22.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
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
                    imageVector = GameIcons.Music,
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
            Text(
                modifier = Modifier.alpha(0.85f),
                text = "${position.roundToInt()}%",
                color = White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )
        }

        VolumeSlider(
            value = position,
            onValueChange = { position = it },
            onValueChangeFinished = { onValueChangeFinished(position) }
        )
    }
}

@Composable
private fun VolumeSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit
) {
    val density = LocalDensity.current
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(26.dp)
    ) {
        val trackWidth = maxWidth
        val widthPx = with(density) { trackWidth.toPx() }
        fun toValue(x: Float) = (x / widthPx * 100f).coerceIn(0f, 100f)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(26.dp)
                .pointerInput(widthPx) {
                    detectTapGestures { offset ->
                        onValueChange(toValue(offset.x))
                        onValueChangeFinished()
                    }
                }
                .pointerInput(widthPx) {
                    detectHorizontalDragGestures(
                        onDragEnd = onValueChangeFinished,
                        onHorizontalDrag = { change, _ -> onValueChange(toValue(change.position.x)) }
                    )
                },
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .background(DeepBlue, CircleShape)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(value / 100f)
                    .height(12.dp)
                    .background(Yellow, CircleShape)
            )
            Box(
                modifier = Modifier
                    .offset(x = trackWidth * (value / 100f) - 13.dp)
                    .size(26.dp)
                    .shadow(2.dp, CircleShape)
                    .background(White, CircleShape)
            )
        }
    }
}

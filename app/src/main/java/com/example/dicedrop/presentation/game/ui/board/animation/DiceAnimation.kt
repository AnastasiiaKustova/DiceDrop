package com.example.dicedrop.presentation.game.ui.board.animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.dicedrop.R
import com.example.dicedrop.domain.entity.DiceItem
import com.example.dicedrop.domain.feedback.GameFeedback
import com.example.dicedrop.presentation.common.LocalGameFeedback
import com.example.dicedrop.presentation.common.DieFace
import com.example.dicedrop.presentation.ui.theme.Orange
import com.example.dicedrop.presentation.ui.theme.ShadowBlue
import com.example.dicedrop.presentation.ui.theme.White
import com.example.dicedrop.presentation.ui.theme.Yellow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

// Кадры кувырка: кубик в изометрии, повёрнутый разными гранями
private val tumbleFrames = listOf(R.drawable.die_roll_123, R.drawable.die_roll_356, R.drawable.die_roll_456)

private const val TUMBLE_MS = 550
private const val SETTLE_MS = 150

@Composable
fun AnimatedDices(
    rolling: Boolean,
    skipAnimation: Boolean = false,
    frozenValue: Int? = null,
    frozenValue2: Int? = null,
    onRollEnd: (result: DiceItem) -> Unit
) {

    val rolling1 = remember { mutableStateOf(rolling) }
    val rolling2 = remember { mutableStateOf(rolling) }

    val result = remember { mutableStateOf(DiceItem.empty()) }

    // Если оба кубика заморожены, броска нет - лёд просто тает
    if (rolling && !skipAnimation && (frozenValue == null || frozenValue2 == null)) {
        DiceRollFeedback()
    }
    // Лёд с замороженных кубиков тает
    if (rolling && (frozenValue != null || frozenValue2 != null)) {
        val feedback = LocalGameFeedback.current
        LaunchedEffect(Unit) { feedback.play(GameFeedback.Sound.BONUS_THAW) }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(26.dp)) {
        AnimatedDie(
            rolling = rolling1.value,
            frozenValue = frozenValue,
            skipAnimation = if (frozenValue != null) true else skipAnimation,
            spin = -1f
        ) {
            rolling1.value = false
            result.value = result.value.copy(
                dice1 = it
            )
        }
        AnimatedDie(
            rolling = rolling2.value,
            frozenValue = frozenValue2,
            skipAnimation = if (frozenValue2 != null) true else skipAnimation,
            spin = 1f
        ) {
            rolling2.value = false
            result.value = result.value.copy(
                dice2 = it
            )
        }
    }

    if (!rolling1.value && !rolling2.value) {
        LaunchedEffect(rolling) {
            onRollEnd(result.value)
        }
    }

}

// Звук броска и лёгкий "тик" вибрации в момент приземления
@Composable
fun DiceRollFeedback() {
    val feedback = LocalGameFeedback.current
    LaunchedEffect(Unit) {
        feedback.play(GameFeedback.Sound.DICE_ROLL)
        delay(TUMBLE_MS.toLong())
        feedback.vibrate(GameFeedback.Haptic.TICK)
    }
}

// Один кубик: подлетает с кувырком, тень под ним сжимается, на приземлении он сплющивается,
// разлетаются искры и грань коротко подсвечивается
@Composable
fun AnimatedDie(
    rolling: Boolean,
    frozenValue: Int? = null,
    skipAnimation: Boolean = false,
    dieSize: Dp = 46.dp,
    jump: Dp = 56.dp,
    spin: Float = 1f,
    pool: List<Int> = (1..6).toList(),
    onRollEnd: (result: Int) -> Unit
) {
    val result = remember { frozenValue ?: pool.random() }

    // Пока кубик в воздухе, показываем кадры кувырка, после приземления - плоскую грань
    var landed by remember { mutableStateOf(frozenValue != null || skipAnimation) }
    var frame by remember { mutableStateOf(0) }
    val flight = remember { Animatable(0f) }
    val squash = remember { Animatable(1f) }
    val sparks = remember { Animatable(0f) }
    val glow = remember { Animatable(0f) }
    val ice = remember { Animatable(if (frozenValue != null) 1f else 0f) }

    LaunchedEffect(rolling) {
        if (!rolling) return@LaunchedEffect

        // Замороженный кубик не бросается: значение остаётся, лёд просто тает
        if (frozenValue != null) {
            ice.animateTo(0f, tween(400))
            onRollEnd(result)
            return@LaunchedEffect
        }

        if (!skipAnimation) {
            landed = false
            flight.snapTo(0f)
            val frames = launch {
                while (true) {
                    delay(90)
                    frame = (frame + 1) % tumbleFrames.size
                }
            }
            flight.animateTo(1f, tween(TUMBLE_MS, easing = LinearEasing))
            frames.cancel()

            landed = true
            squash.snapTo(0f)
            launch { sparks.snapTo(0f); sparks.animateTo(1f, tween(350)) }
            launch { glow.snapTo(1f); glow.animateTo(0f, tween(450)) }
            squash.animateTo(
                1f,
                spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
            )
        }

        landed = true
        delay(SETTLE_MS.toLong())
        onRollEnd(result)
    }

    Box(
        modifier = Modifier.size(dieSize),
        contentAlignment = Alignment.Center
    ) {
        val p = flight.value
        val air = if (landed) 0f else sin(PI * p).toFloat()

        // Тень на "полу": чем выше кубик, тем она меньше и прозрачнее
        Box(
            modifier = Modifier
                .size(dieSize)
                .drawBehind {
                    val k = 1f - 0.55f * air
                    val w = size.width * 0.8f * k
                    val h = 9.dp.toPx() * k
                    drawOval(
                        color = ShadowBlue.copy(alpha = 0.45f * k),
                        topLeft = Offset((size.width - w) / 2, size.height + 2.dp.toPx()),
                        size = Size(w, h)
                    )
                }
        )

        if (!landed) {
            Image(
                painter = painterResource(tumbleFrames[frame]),
                contentDescription = null,
                modifier = Modifier
                    .size(dieSize * 1.1f)
                    .graphicsLayer {
                        // Стартует чуть ниже своего места, взлетает по дуге и опускается на место
                        translationY = (1f - p) * 24.dp.toPx() - air * jump.toPx()
                        rotationZ = spin * p * 540f
                    }
            )
        } else {
            DieFace(
                value = result,
                size = dieSize,
                glow = glow.value,
                modifier = Modifier.graphicsLayer {
                    // squash: 0 - сплющен при ударе, 1 - обычная форма
                    val s = squash.value
                    scaleX = 1.18f - 0.18f * s
                    scaleY = 0.82f + 0.18f * s
                    transformOrigin = TransformOrigin(0.5f, 1f)
                }
            )
            if (sparks.value in 0.001f..0.999f) {
                Sparks(progress = sparks.value, dieSize = dieSize)
            }
        }

        if (ice.value > 0.02f) {
            Image(
                painter = painterResource(id = R.drawable.ic_ice_shell),
                contentDescription = null,
                modifier = Modifier
                    .requiredSize(dieSize * 1.3f)
                    .graphicsLayer { alpha = 0.75f * ice.value }
            )
        }
    }
}

// Три искры разлетаются от места приземления и гаснут
@Composable
private fun Sparks(progress: Float, dieSize: Dp) {
    val d = 14.dp * progress
    val alpha = 1f - progress
    Spark(x = -dieSize / 2 - d, y = dieSize / 4, size = 9.dp, color = Yellow, alpha = alpha, round = false)
    Spark(x = dieSize / 2 + d, y = dieSize / 5, size = 8.dp, color = White, alpha = alpha * 0.8f, round = true)
    Spark(x = 0.dp, y = -dieSize / 2 - d, size = 7.dp, color = Orange, alpha = alpha, round = false)
}

@Composable
private fun Spark(x: Dp, y: Dp, size: Dp, color: Color, alpha: Float, round: Boolean) {
    Box(
        modifier = Modifier
            .offset(x = x, y = y)
            .size(size)
            .graphicsLayer {
                this.alpha = alpha
                rotationZ = 25f
            }
            .background(color, if (round) CircleShape else RoundedCornerShape(2.dp))
    )
}

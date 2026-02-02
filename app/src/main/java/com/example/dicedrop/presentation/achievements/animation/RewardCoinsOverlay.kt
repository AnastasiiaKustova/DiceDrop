package com.example.dicedrop.presentation.achievements.animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ConfettiOverlay(
    data: RewardAnimationData?,
    onFinished: () -> Unit,
    particleCount: Int = 12
) {
    if (data == null) return

    val particles = remember { generateConfetti(particleCount) }
    val animProgress = remember { Animatable(0f) }

    val start = data.start ?: Offset.Zero

    LaunchedEffect(data) {
        animProgress.snapTo(0f)
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(1000, easing = FastOutSlowInEasing)
        )
        onFinished()
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {}
    ) {
        particles.forEach { particle ->
            val distance = particle.speed * animProgress.value * 2
            val rad = Math.toRadians(particle.angle.toDouble())
            val x = start.x + (distance * cos(rad)).toFloat()
            val y = start.y - (distance * sin(rad)).toFloat()

            drawCircle(
                color = particle.color.copy(alpha = 1f - animProgress.value),
                radius = particle.size,
                center = Offset(x, y)
            )
        }
    }
}

data class ConfettiParticle(
    val angle: Float,
    val speed: Float,
    val color: Color,
    val size: Float,
    val lifetime: Float // 0..1
)

fun generateConfetti(count: Int): List<ConfettiParticle> {
    val colors = listOf(Color.Yellow, Color.Red, Color.Green, Color.Cyan, Color.Magenta)
    return List(count) {
        ConfettiParticle(
            angle = (0..360).random().toFloat(),
            speed = (400..600).random().toFloat(),
            color = colors.random(),
            size = (6..12).random().toFloat(),
            lifetime = 1f
        )
    }
}
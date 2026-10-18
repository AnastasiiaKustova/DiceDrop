package com.example.dicedrop.presentation.common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp
import com.example.dicedrop.presentation.ui.theme.Green
import com.example.dicedrop.presentation.ui.theme.LightBlue
import com.example.dicedrop.presentation.ui.theme.Orange
import com.example.dicedrop.presentation.ui.theme.White
import com.example.dicedrop.presentation.ui.theme.Yellow
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

// Цвета конфетти из макетов; жёлтый встречается чаще
private val confettiColors = listOf(Orange, Green, Yellow, LightBlue, White, Yellow)

private const val BURST_MS = 1600
// Торможение о воздух и сила тяжести (в dp)
private const val DRAG = 2.4f
private const val GRAVITY = 1100f

private enum class PieceShape { SQUARE, STRIP, DOT }

private class ConfettiPiece(
    val vx: Float,
    val vy: Float,
    val size: Float,
    val shape: PieceShape,
    val color: Color,
    val rotation: Float,
    val spin: Float,
    val flip: Float
)

private fun makePieces(count: Int, power: Float): List<ConfettiPiece> = List(count) {
    // Большая часть летит веером вверх, остальные - во все стороны
    val angle = if (Random.nextFloat() < 0.75f) {
        Random.nextDouble(200.0, 340.0)
    } else {
        Random.nextDouble(0.0, 360.0)
    } * PI / 180
    val speed = (380f + Random.nextFloat() * 440f) * power
    val r = Random.nextFloat()
    ConfettiPiece(
        vx = (cos(angle) * speed).toFloat(),
        vy = (sin(angle) * speed).toFloat(),
        size = 7f + Random.nextFloat() * 8f,
        shape = when {
            r < 0.45f -> PieceShape.SQUARE
            r < 0.8f -> PieceShape.STRIP
            else -> PieceShape.DOT
        },
        color = confettiColors.random(),
        rotation = Random.nextFloat() * 360f,
        spin = Random.nextFloat() * 720f - 360f,
        flip = 6f + Random.nextFloat() * 8f
    )
}

// Взрыв конфетти: кусочки разлетаются из точки origin (в пикселях внутри области,
// по умолчанию - центр), кувыркаются, падают и тают. Перезапускается при смене key.
// Рисует за пределами своей области, поэтому её можно накладывать прямо на карточку
@Composable
fun ConfettiBurst(
    key: Any?,
    modifier: Modifier = Modifier,
    count: Int = 40,
    power: Float = 1f,
    origin: Offset? = null,
    onFinished: () -> Unit = {}
) {
    val pieces = remember(key) { makePieces(count, power) }
    val progress = remember(key) { Animatable(0f) }

    LaunchedEffect(key) {
        progress.animateTo(1f, tween(BURST_MS, easing = LinearEasing))
        onFinished()
    }

    Canvas(modifier = modifier) {
        val p = progress.value
        if (p >= 1f) return@Canvas
        val t = p * BURST_MS / 1000f
        val travel = (1f - exp(-DRAG * t)) / DRAG
        val fall = GRAVITY * t * t / 2
        val alpha = if (p < 0.7f) 1f else 1f - (p - 0.7f) / 0.3f
        val start = origin ?: center

        pieces.forEach { piece ->
            val x = start.x + piece.vx.dp.toPx() * travel
            val y = start.y + (piece.vy * travel + fall).dp.toPx()
            val s = piece.size.dp.toPx()
            translate(x, y) {
                rotate(piece.rotation + piece.spin * t, pivot = Offset.Zero) {
                    // Сжатие по ширине имитирует переворот кусочка в воздухе
                    scale(cos(piece.flip * t), 1f, pivot = Offset.Zero) {
                        when (piece.shape) {
                            PieceShape.DOT -> drawCircle(
                                color = piece.color,
                                radius = s * 0.4f,
                                center = Offset.Zero,
                                alpha = alpha
                            )

                            PieceShape.SQUARE -> drawRoundRect(
                                color = piece.color,
                                topLeft = Offset(-s / 2, -s / 2),
                                size = Size(s, s),
                                cornerRadius = CornerRadius(s * 0.25f),
                                alpha = alpha
                            )

                            PieceShape.STRIP -> drawRoundRect(
                                color = piece.color,
                                topLeft = Offset(-s / 2, -s / 4),
                                size = Size(s, s / 2),
                                cornerRadius = CornerRadius(s * 0.15f),
                                alpha = alpha
                            )
                        }
                    }
                }
            }
        }
    }
}

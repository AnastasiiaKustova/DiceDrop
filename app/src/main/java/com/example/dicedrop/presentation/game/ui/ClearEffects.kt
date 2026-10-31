package com.example.dicedrop.presentation.game.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp
import com.example.dicedrop.domain.feedback.GameFeedback
import com.example.dicedrop.presentation.common.FlyingCoins
import com.example.dicedrop.presentation.common.MAX_FLYING_COINS
import com.example.dicedrop.presentation.game.ui.drag.BoardUiState
import com.example.dicedrop.presentation.ui.theme.Orange
import com.example.dicedrop.presentation.ui.theme.White
import com.example.dicedrop.presentation.ui.theme.Yellow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

// Осколки клеток при очистке линий: летят в стороны и падают
private class Shard(val x: Float, val y: Float, val vx: Float, val vy: Float, val size: Float, val spin: Float, val color: Color)
private class Burst(val shards: List<Shard>) {
    val time = Animatable(0f)
}

private const val SHARD_LIFE = 0.75f

// Эффекты очистки поверх экрана игры: осколки очищенных клеток и монеты, летящие в счётчик.
// Всё считается от измеренных положений поля и счётчика, поэтому одинаково работает
// в портрете и на планшете в альбомной ориентации
@Stable
class ClearEffects {
    private val bursts = mutableStateListOf<Burst>()

    val coinFlight = FlyingCoins()

    // Поле игры (положение, размер клетки, число строк и столбцов) - в координатах корня
    var board: BoardUiState? = null
    var boardRows = 0
    var boardCols = 0
    private var origin = Offset.Zero

    suspend fun play(
        rows: List<Int>,
        cols: List<Int>,
        coinsEarned: Int,
        density: Float,
        feedback: GameFeedback
    ) = coroutineScope {
        val board = board ?: return@coroutineScope
        val cell = board.cellSize.value * density
        val cells = (rows.flatMap { r -> (0 until boardCols).map { c -> r to c } } +
                cols.flatMap { c -> (0 until boardRows).map { r -> r to c } }).distinct()
        if (cells.isEmpty()) return@coroutineScope
        val centers = cells.map { (r, c) ->
            Offset(board.boardOffsetX + (c + 0.5f) * cell, board.boardOffsetY + (r + 0.5f) * cell)
        }
        launch { shatter(centers.map { it - origin }, density) }

        if (coinsEarned <= 0) return@coroutineScope
        // Монеты стартуют из середины очищенных клеток
        val from = Offset(centers.map { it.x }.average().toFloat(), centers.map { it.y }.average().toFloat())
        launch {
            delay(250)
            // Один звук на всю очистку - по прилёту первой монеты
            coinFlight.fly(from, min(coinsEarned, MAX_FLYING_COINS), density) { i ->
                if (i == 0) feedback.play(GameFeedback.Sound.COIN)
            }
        }
    }

    private suspend fun shatter(centers: List<Offset>, density: Float) {
        val colors = listOf(Yellow, Yellow, Orange, White)
        val burst = Burst(centers.flatMap { center ->
            List(3) {
                val angle = Random.nextFloat() * 2f * PI.toFloat()
                val speed = (120f + Random.nextFloat() * 260f) * density
                Shard(
                    x = center.x, y = center.y,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed - 320f * density,
                    size = (5f + Random.nextFloat() * 6f) * density,
                    spin = (Random.nextFloat() - 0.5f) * 1440f,
                    color = colors[Random.nextInt(colors.size)]
                )
            }
        })
        bursts += burst
        burst.time.animateTo(1f, tween((SHARD_LIFE * 1000).toInt(), easing = LinearEasing))
        bursts -= burst
    }

    @Composable
    fun Overlay(modifier: Modifier = Modifier) {
        Box(modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { origin = it.positionInRoot() }
        ) {
            Shards()
            coinFlight.Layer()
        }
    }

    // Осколки очищенных клеток: разлетаются вверх и в стороны, падают, вращаются и тают
    @Composable
    private fun Shards() {
        if (bursts.isEmpty()) return
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gravity = 1600.dp.toPx()
            bursts.forEach { burst ->
                val t = burst.time.value * SHARD_LIFE
                val fade = (1f - t / SHARD_LIFE).coerceIn(0f, 1f)
                burst.shards.forEach { sh ->
                    val x = sh.x + sh.vx * t
                    val y = sh.y + sh.vy * t + gravity * t * t / 2
                    rotate(sh.spin * t, pivot = Offset(x, y)) {
                        drawRoundRect(
                            color = sh.color.copy(alpha = fade),
                            topLeft = Offset(x - sh.size / 2, y - sh.size / 2),
                            size = Size(sh.size, sh.size),
                            cornerRadius = CornerRadius(sh.size * 0.25f)
                        )
                    }
                }
            }
        }
    }
}

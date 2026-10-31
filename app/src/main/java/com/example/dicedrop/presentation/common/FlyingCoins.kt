package com.example.dicedrop.presentation.common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.dicedrop.R
import com.example.dicedrop.domain.feedback.GameFeedback
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.math.roundToInt

private const val COIN_FLIGHT_MS = 650L
private const val COIN_STAGGER_MS = 70L
const val MAX_FLYING_COINS = 8

// Монета, которая летит в счётчик монет
private class FlyingCoin(val from: Offset, val to: Offset) {
    val progress = Animatable(0f)
}

// Монеты, летящие дугой в счётчик: его положение измеряется, поэтому работает на любой раскладке.
// Счётчик подпрыгивает на каждую прилетевшую монету
@Stable
class FlyingCoins {
    private val coins = mutableStateListOf<FlyingCoin>()
    private val bump = Animatable(1f)

    // Центр монеты в счётчике и начало слоя - в координатах корня
    private var target: Offset? = null
    private var origin = Offset.Zero

    // Монеты зачисляются сразу, а счётчик показывает их по мере прилёта
    private var heldCoins by mutableStateOf<Int?>(null)
    private var flightsInAir = 0

    fun coinsToShow(coins: Int): Int = heldCoins ?: coins

    // Счётчик замирает на сумме до награды; вызывать сразу, не дожидаясь начала полёта
    fun holdCoins(coinsBefore: Int) {
        if (heldCoins == null) heldCoins = coinsBefore
        flightsInAir++
    }

    // Сколько длится полёт монет награды
    fun rewardFlightMs(amount: Int): Long =
        (min(amount, MAX_FLYING_COINS) - 1) * COIN_STAGGER_MS + COIN_FLIGHT_MS

    // Монеты награды летят в счётчик, и число растёт на каждую. Перед этим вызвать holdCoins
    suspend fun flyReward(from: Offset, amount: Int, density: Float, feedback: GameFeedback) {
        val count = min(amount, MAX_FLYING_COINS)
        fly(from, count, density) { i ->
            // Части награды в сумме дают её целиком, даже если она не делится на число монет
            val part = amount * (i + 1) / count - amount * i / count
            heldCoins = heldCoins?.plus(part)
            // Один звук на всю награду - по прилёту первой монеты
            if (i == 0) feedback.play(GameFeedback.Sound.COIN)
        }
        flightsInAir--
        if (flightsInAir == 0) heldCoins = null
    }

    // from - в координатах корня; onArrive получает номер прилетевшей монеты
    suspend fun fly(from: Offset, count: Int, density: Float, onArrive: (Int) -> Unit = {}) = coroutineScope {
        val to = target ?: return@coroutineScope
        repeat(count) { i ->
            launch {
                delay(i * COIN_STAGGER_MS)
                val coin = FlyingCoin(from - origin + Offset((i - count / 2) * 10f * density, 0f), to - origin)
                coins += coin
                coin.progress.animateTo(1f, tween(COIN_FLIGHT_MS.toInt(), easing = FastOutSlowInEasing))
                coins -= coin
                onArrive(i)
                bump.snapTo(1.15f)
                bump.animateTo(1f, tween(180))
            }
        }
    }

    // Модификатор счётчика монет: запоминает, куда лететь монетам, и подпрыгивает при их прилёте.
    // Монета в плашке стоит у левого края, примерно на половину высоты плашки от него
    fun Modifier.coinsTarget(): Modifier = this
        .onGloballyPositioned {
            val bounds = it.boundsInRoot()
            target = Offset(bounds.left + bounds.height * 0.45f, bounds.center.y)
        }
        .graphicsLayer {
            scaleX = bump.value
            scaleY = bump.value
        }

    @Composable
    fun Layer() {
        val density = LocalDensity.current
        val size = 26.dp
        val half = with(density) { (size / 2).roundToPx() }
        Box(modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { origin = it.positionInRoot() }
        ) {
            coins.forEach { coin ->
                val p = coin.progress.value
                if (p <= 0f || p >= 1f) return@forEach
                // Дуга: сначала вверх и в сторону от счётчика, потом к нему
                val side = if (coin.to.x < coin.from.x) 1f else -1f
                val control = Offset(
                    coin.from.x + side * with(density) { 90.dp.toPx() },
                    coin.from.y - with(density) { 120.dp.toPx() }
                )
                val u = 1f - p
                val at = coin.from * (u * u) + control * (2 * u * p) + coin.to * (p * p)
                Image(
                    painter = painterResource(R.drawable.coin_star),
                    contentDescription = null,
                    modifier = Modifier
                        .offset { IntOffset(at.x.roundToInt() - half, at.y.roundToInt() - half) }
                        .size(size)
                        .graphicsLayer {
                            val s = 1.2f - 0.4f * p
                            scaleX = s
                            scaleY = s
                        }
                )
            }
        }
    }
}

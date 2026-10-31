package com.example.dicedrop.presentation.achievements.animation

import androidx.annotation.DrawableRes
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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.dicedrop.domain.feedback.GameFeedback
import com.example.dicedrop.presentation.common.ConfettiBurst
import com.example.dicedrop.presentation.common.FlyingCoins
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

// Карточка забранного достижения остаётся на месте, пока идёт анимация:
// сначала с кнопкой (монеты в полёте), потом с галочкой, и только затем уезжает в конец списка
enum class RewardStage { FLYING, DONE }

// Награда и откуда разлетаются её части - в координатах корня
class RewardClaim(
    val achievementId: String,
    val coins: Int,
    val buttonCenter: Offset,
    val coinsFrom: Offset,
    val bonuses: List<Pair<Int, Offset>>
)

// Значок бонуса из награды: подпрыгивает, увеличивается и тает
private class BonusPop(@DrawableRes val iconRes: Int, val at: Offset) {
    val progress = Animatable(0f)
}

// Эффекты получения награды поверх экрана достижений. Каждая награда живёт отдельно,
// поэтому можно забирать несколько подряд, не дожидаясь конца анимации
@Stable
class RewardEffects {
    val coinFlight = FlyingCoins()
    val stages = mutableStateMapOf<String, RewardStage>()

    private val bursts = mutableStateListOf<RewardClaim>()
    private val pops = mutableStateListOf<BonusPop>()
    private var origin = Offset.Zero

    fun play(scope: CoroutineScope, claim: RewardClaim, coinsBefore: Int, density: Float, feedback: GameFeedback) {
        stages[claim.achievementId] = RewardStage.FLYING
        coinFlight.holdCoins(coinsBefore)
        bursts += claim

        claim.bonuses.forEach { (iconRes, at) ->
            scope.launch { popBonus(BonusPop(iconRes, at)) }
        }
        scope.launch {
            delay(150)
            // Галочка - на середине полёта монет, не дожидаясь, пока долетят все
            val check = launch {
                delay(coinFlight.rewardFlightMs(claim.coins) / 2)
                stages[claim.achievementId] = RewardStage.DONE
            }
            coinFlight.flyReward(claim.coinsFrom, claim.coins, density, feedback)
            check.cancel()
            stages[claim.achievementId] = RewardStage.DONE
            delay(120)
            stages.remove(claim.achievementId)
        }
    }

    private suspend fun popBonus(pop: BonusPop) {
        delay(100)
        pops += pop
        pop.progress.animateTo(1f, tween(800, easing = FastOutSlowInEasing))
        pops -= pop
    }

    @Composable
    fun Overlay() {
        Box(modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { origin = it.positionInRoot() }
        ) {
            bursts.forEach { claim ->
                key(claim) {
                    ConfettiBurst(
                        key = claim,
                        modifier = Modifier.fillMaxSize(),
                        count = 46,
                        origin = claim.buttonCenter - origin,
                        onFinished = { bursts -= claim }
                    )
                }
            }
            pops.forEach { pop -> BonusPopImage(pop) }
            coinFlight.Layer()
        }
    }

    @Composable
    private fun BonusPopImage(pop: BonusPop) {
        val density = LocalDensity.current
        val size = 40.dp
        val half = with(density) { (size / 2).roundToPx() }
        val rise = with(density) { 80.dp.toPx() }
        Image(
            painter = painterResource(pop.iconRes),
            contentDescription = null,
            modifier = Modifier
                .offset {
                    IntOffset(
                        (pop.at.x - origin.x).roundToInt() - half,
                        (pop.at.y - origin.y - rise * pop.progress.value).roundToInt() - half
                    )
                }
                .size(size)
                .graphicsLayer {
                    val p = pop.progress.value
                    val s = 1f + 0.7f * p
                    scaleX = s
                    scaleY = s
                    alpha = if (p < 0.6f) 1f else (1f - p) / 0.4f
                }
        )
    }
}

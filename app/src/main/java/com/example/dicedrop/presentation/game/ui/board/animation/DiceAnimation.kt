package com.example.dicedrop.presentation.game.ui.board.animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.dicedrop.R
import com.example.dicedrop.domain.entity.DiceItem
import com.example.dicedrop.presentation.game.ui.board.toDiceRes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    var result = remember { mutableStateOf(DiceItem.empty()) }

    Row {
        AnimatedDie(
            rolling = rolling1.value,
            frozenValue = frozenValue,
            skipAnimation = if (frozenValue != null) true else skipAnimation,
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
        ) {


            rolling2.value = false
            result.value = result.value.copy(
                dice2 = it
            )
        }
    }

    if (!rolling1.value && !rolling2.value) {
        LaunchedEffect(rolling) {
            // Если оба кубика заморожены, броска не было и ждать нечего
            if (frozenValue == null || frozenValue2 == null) delay(400)
            onRollEnd(result.value)
        }
    }

}

@Composable
fun AnimatedDie(
    rolling: Boolean,
    frozenValue: Int? = null,
    skipAnimation: Boolean = false,
    imageSize: Dp = 50.dp,
    boxPadding: Dp = 25.dp,
    pool: List<Int> = (1..6).toList(),
    onRollEnd: (result: Int) -> Unit
) {
    var currentImage by remember { mutableStateOf(frozenValue?.toDiceRes() ?: R.drawable.dice_1) }

    val rotation = remember { Animatable(0f) }
    val scale = remember { Animatable(1f) }
    val ice = remember { Animatable(if (frozenValue != null) 1f else 0f) }

    val result = remember { frozenValue ?: pool.random() }

    LaunchedEffect(rolling) {
        if (rolling) {

            rotation.snapTo(0f)
            scale.snapTo(1f)

            // Замороженный кубик не бросается: значение остается, лед просто тает
            if (frozenValue != null) {
                currentImage = result.toDiceRes()
                ice.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(400)
                )
                onRollEnd(result)
                return@LaunchedEffect
            }
            val duration = 1200
            val steps = 10

            if (!skipAnimation) {
                val job1 = launch {
                    repeat(steps) {
                        currentImage = when ((1..5).random()) {
                            1 -> R.drawable.dice_123
                            2 -> R.drawable.dice_135
                            3 -> R.drawable.dice_145
                            4 -> R.drawable.dice_356
                            5 -> R.drawable.dice_456
                            else -> R.drawable.dice_124
                        }

                        rotation.animateTo(
                            targetValue = rotation.value + 180f,
                            animationSpec = tween(duration / steps)
                        )
                    }
                }

                val job2 = launch {
                    scale.animateTo(
                        targetValue = 2f,
                        animationSpec = tween(duration / 2)
                    )
                    scale.animateTo(
                        targetValue = 1.5f,
                        animationSpec = tween(duration / 2)
                    )
                }

                job1.join()
                job2.join()
            }

            currentImage = result.toDiceRes()

            scale.snapTo(1f)

            scale.animateTo(
                targetValue = 1.2f,
                animationSpec = tween(duration / 4)
            )

            delay(200)
            onRollEnd(result)
        }
    }

    Dice(
        imageID = currentImage,
        imageSize = imageSize,
        boxPadding = boxPadding,
        showIce = frozenValue != null,
        iceAlpha = { ice.value },
        modifier = Modifier
            .size(imageSize)
            .graphicsLayer {
                rotationZ = rotation.value
                scaleX = scale.value
                scaleY = scale.value
            }
    )
}

@Composable
private fun Dice(
    modifier: Modifier = Modifier,
    imageID: Int,
    imageSize: Dp = 50.dp,
    boxPadding: Dp = 25.dp,
    showIce: Boolean = false,
    iceAlpha: () -> Float = { 0f }
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(boxPadding)
    ) {
        Image(
            modifier = modifier,
            painter = painterResource(id = imageID),
            contentDescription = ""
        )
        if (showIce) {
            Image(
                painter = painterResource(id = R.drawable.ic_ice_shell),
                contentDescription = null,
                modifier = Modifier
                    .size(imageSize * 1.4f)
                    .graphicsLayer { alpha = 0.75f * iceAlpha() }
            )
        }
    }
}
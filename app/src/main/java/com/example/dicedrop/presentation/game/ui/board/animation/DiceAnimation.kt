package com.example.dicedrop.presentation.game.ui.board.animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.dicedrop.R
import com.example.dicedrop.domain.entity.DiceItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AnimatedDices(
    rolling: Boolean,
    skipAnimation: Boolean = false,
    frozenValue: Int? = null,
    onRollEnd: (result: DiceItem) -> Unit
) {

    val rolling1 = remember { mutableStateOf(rolling) }
    val rolling2 = remember { mutableStateOf(rolling) }

    var result = remember { mutableStateOf(DiceItem.empty()) }

    Row {
        AnimatedDice(
            rolling = rolling1.value,
            frozenValue = frozenValue,
            skipAnimation = if (frozenValue != null) true else skipAnimation,
        ) {
            rolling1.value = false
            result.value = result.value.copy(
                dice1 = it
            )
        }
        AnimatedDice(
            rolling = rolling2.value,
            skipAnimation = skipAnimation,
        ) {
            rolling2.value = false
            result.value = result.value.copy(
                dice2 = it
            )
        }
    }

    if (!rolling1.value && !rolling2.value) {
        LaunchedEffect(rolling) {
            delay(400)
            onRollEnd(result.value)
        }
    }

}

@Composable
private fun AnimatedDice(
    rolling: Boolean,
    frozenValue: Int? = null,
    skipAnimation: Boolean = false,
    onRollEnd: (result: Int) -> Unit
) {
    var currentImage by remember { mutableStateOf(R.drawable.dice_1) }

    val rotation = remember { Animatable(0f) }
    val scale = remember { Animatable(1f) }

    val result = frozenValue ?: (1..6).random()

    LaunchedEffect(rolling) {
        if (rolling) {

            rotation.snapTo(0f)
            scale.snapTo(1f)
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

            currentImage = when (result) {
                1 -> R.drawable.dice_1
                2 -> R.drawable.dice_2
                3 -> R.drawable.dice_3
                4 -> R.drawable.dice_4
                5 -> R.drawable.dice_5
                else -> R.drawable.dice_6
            }

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
        modifier = Modifier
            .size(50.dp)
            .graphicsLayer(
                rotationZ = rotation.value,
                scaleX = scale.value,
                scaleY = scale.value
            )
    )
}

@Composable
private fun Dice(
    modifier: Modifier = Modifier,
    imageID: Int
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(25.dp)
    ) {
        Image(
            modifier = modifier,
            painter = painterResource(id = imageID),
            contentDescription = ""
        )
    }
}
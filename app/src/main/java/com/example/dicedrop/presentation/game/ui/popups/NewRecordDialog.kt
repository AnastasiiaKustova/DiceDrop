package com.example.dicedrop.presentation.game.ui.popups

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
import com.example.dicedrop.presentation.achievements.animation.ConfettiOverlay
import com.example.dicedrop.presentation.achievements.animation.ConfettiParticle
import com.example.dicedrop.presentation.achievements.animation.RewardAnimationData
import com.example.dicedrop.presentation.achievements.animation.generateConfetti
import com.example.dicedrop.presentation.animations.AnimatedButton
import com.example.dicedrop.presentation.ui.theme.DarkBlue
import com.example.dicedrop.presentation.ui.theme.White
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun NewRecordDialog(
    modifier: Modifier = Modifier,
    record: Int,
    confirmClick: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x88000000)),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = scaleIn(
                initialScale = 0.8f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            DialogContent(
                modifier = modifier,
                record = record,
                confirmClick = confirmClick
            )
        }
    }
}

@Composable
private fun DialogContent(
    modifier: Modifier,
    record: Int,
    confirmClick: () -> Unit
) {
    var dialogSize by remember { mutableStateOf(IntSize.Zero) }

    // Список активных частиц
    val particles = remember { mutableStateListOf<ConfettiParticle>() }

    // Постоянная генерация новых частиц
    LaunchedEffect(Unit) {
        while (true) {
            if (dialogSize.width > 0 && dialogSize.height > 0) {
                particles += ConfettiParticle(
                    angle = (0..180).random().toFloat(),
                    speed = (200..400).random().toFloat(),
                    color = listOf(Color.Yellow, Color.Red, Color.Green, Color.Cyan, Color.Magenta).random(),
                    size = (6..12).random().toFloat(),
                    lifetime = 0f
                )
            }
            delay(100)
        }
    }

    // Обновление частиц
    LaunchedEffect(Unit) {
        while (true) {
            val delta = 16L
            for (i in particles.indices.reversed()) {
                val p = particles[i]
                val newLifetime = p.lifetime + 0.02f
                if (newLifetime > 1f) {
                    particles.removeAt(i)
                } else {
                    particles[i] = p.copy(lifetime = newLifetime)
                }
            }
            delay(delta)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth(0.85f)
            .fillMaxHeight(0.5f)
            .background(White, RoundedCornerShape(20.dp))
            //.background(Color.Transparent)
            .border(
                width = 5.dp,
                color = DarkBlue,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(16.dp)
            .onGloballyPositioned { layout -> dialogSize = layout.size },
        contentAlignment = Alignment.Center
    ) {
        // ----------------------
        // Canvas с конфетти
        // ----------------------
        Canvas(modifier = Modifier.fillMaxSize()) {
            val start = Offset(dialogSize.width / 1f, dialogSize.height / 1f)
            particles.forEach { particle ->
                val distance = particle.speed * particle.lifetime
                val rad = -Math.toRadians(particle.angle.toDouble())
                val x = start.x + (distance * cos(rad)).toFloat()
                val y = start.y - (distance * sin(rad)).toFloat()
                drawCircle(
                    color = particle.color.copy(alpha = 1f - particle.lifetime),
                    radius = particle.size,
                    center = Offset(x, y)
                )
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val start = Offset(0f, dialogSize.height / 1f)
            particles.forEach { particle ->
                val distance = particle.speed * particle.lifetime
                val rad = -Math.toRadians(particle.angle.toDouble())
                val x = start.x + (distance * cos(rad)).toFloat()
                val y = start.y - (distance * sin(rad)).toFloat()
                drawCircle(
                    color = particle.color.copy(alpha = 1f - particle.lifetime),
                    radius = particle.size,
                    center = Offset(x, y)
                )
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val start = Offset(dialogSize.width / 2f, -40f)
            particles.forEach { particle ->
                val distance = particle.speed * particle.lifetime
                val rad = Math.toRadians(particle.angle.toDouble())
                val x = start.x + (distance * cos(rad)).toFloat()
                val y = start.y - (distance * sin(rad)).toFloat()
                drawCircle(
                    color = particle.color.copy(alpha = 1f - particle.lifetime),
                    radius = particle.size,
                    center = Offset(x, y)
                )
            }
        }

        // ----------------------
        // Содержимое диалога
        // ----------------------
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            //Spacer(modifier = Modifier.weight(1f))

            Box {
                var imageHeight by remember { mutableStateOf(0) }
                var imageWidth by remember { mutableStateOf(0) }

                val density = LocalDensity.current

                Image(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .onGloballyPositioned { layoutCoordinates ->
                            imageHeight = layoutCoordinates.size.height
                            imageWidth = layoutCoordinates.size.width
                        },
                    painter = painterResource(id = R.drawable.pic_new_record),
                    contentDescription = null
                )

                Text(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset { IntOffset(x = 0, y = (imageHeight * 0.27f).toInt()) },
                    text = "NEW RECORD",
                    color = White,
                    fontWeight = FontWeight.Bold,
                    fontSize = with(density) { (imageWidth * 0.07f).toSp() } // 10% от ширины картинки
                )
            }


            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Счет $record",
                color = DarkBlue,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            AnimatedButton(
                text = "ПРОДОЛЖИТЬ",
                modifier = Modifier.wrapContentHeight(),
                onClick = { _ -> confirmClick() }
            )

        }
    }
}

@Composable
@Preview
fun NewRecordPreview(){
    NewRecordDialog(
        record = 100,
        confirmClick = {}
    )
}
package com.example.dicedrop.presentation.game.ui.popups

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.dicedrop.R
import com.example.dicedrop.domain.entity.BonusItem
import com.example.dicedrop.presentation.achievements.animation.ConfettiParticle
import com.example.dicedrop.presentation.game.ui.board.animation.particles.Particles
import com.example.dicedrop.presentation.game.ui.board.animation.particles.ParticlesCanvas
import com.example.dicedrop.presentation.ui.theme.White
import kotlinx.coroutines.delay

@Composable
fun NewRecordDialog(
    record: Int,
    rewardsList: List<BonusItem>,
    confirmClick: () -> Unit
) {
    var visible by remember { mutableStateOf(true) }
    var dialogSize by remember { mutableStateOf(IntSize.Zero) }
    val particles = remember { mutableStateListOf<ConfettiParticle>() }

    val density = LocalDensity.current.density
    val screenHeightPx = LocalConfiguration.current.screenHeightDp * density
    val fillHeightCoefficient = getFillHeightCoefficient(screenHeightPx)

    LaunchedEffect(Unit) {
        visible = true
    }

    LaunchedEffect(Unit) {
        delay(100)
        Particles.generateParticles(dialogSize, particles)
    }

    LaunchedEffect(Unit) {
        Particles.moveParticles(particles)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x88000000))
            .clickable { confirmClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        AnimatedVisibility(
            visible = visible,
            enter = scaleIn(
                initialScale = 0.2f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .padding(top = 90.dp)
                            .fillMaxWidth(0.65f)
                            .fillMaxHeight(fillHeightCoefficient)
                            .background(White, RoundedCornerShape(20.dp))
                            .onGloballyPositioned { layout -> dialogSize = layout.size }
                            ,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {

                        Spacer(
                            modifier = Modifier.height(100.dp)
                        )

                        NewRecordContent(
                            record = record.toString(),
                            rewardsList = rewardsList)

                        ParticlesCanvas(dialogSize, particles)
                    }
                    Image(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth(0.9f),
                        painter = painterResource(id = R.drawable.ic_new_record),
                        contentDescription = null
                    )
                }

                Text("Tap to continue", color = Color.White)
            }
        }
    }
}

private fun getFillHeightCoefficient(screenHeight: Float): Float {
    val m = -0.000833f

    return (m * screenHeight + 2.26f).coerceIn(0f, 1f)
}
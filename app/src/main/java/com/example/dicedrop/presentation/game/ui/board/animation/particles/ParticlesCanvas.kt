package com.example.dicedrop.presentation.game.ui.board.animation.particles

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.example.dicedrop.presentation.achievements.animation.ConfettiParticle
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ParticlesCanvas(
    dialogSize: IntSize,
    particles: SnapshotStateList<ConfettiParticle>,
){
    Canvas(modifier = Modifier.fillMaxSize()) {
        val start = Offset(dialogSize.width / 2f, -dialogSize.height.toFloat())
        particles.forEach { particle ->
            val distance = particle.speed * particle.lifetime * 1.5
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
}
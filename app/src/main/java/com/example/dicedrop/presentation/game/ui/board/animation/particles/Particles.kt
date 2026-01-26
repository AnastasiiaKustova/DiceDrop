package com.example.dicedrop.presentation.game.ui.board.animation.particles

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import com.example.dicedrop.presentation.achievements.animation.ConfettiParticle
import kotlinx.coroutines.delay

object Particles {
    suspend fun generateParticles(
        dialogSize: IntSize,
        particles: SnapshotStateList<ConfettiParticle>,
    ){
        while (true) {
            if (dialogSize.width > 0 && dialogSize.height > 0) {
                particles += ConfettiParticle(
                    angle = (0..180).random().toFloat(),
                    speed = (200..400).random().toFloat(),
                    color = listOf(
                        Color.Yellow,
                        Color.Red,
                        Color.Green,
                        Color.Cyan,
                        Color.Magenta
                    ).random(),
                    size = (6..12).random().toFloat(),
                    lifetime = 0f
                )
            }
            delay(50)
        }
    }

    suspend fun moveParticles(
        particles: SnapshotStateList<ConfettiParticle>,
    ){
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
}
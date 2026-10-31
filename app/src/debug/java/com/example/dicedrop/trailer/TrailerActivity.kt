package com.example.dicedrop.trailer

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.dicedrop.domain.feedback.GameFeedback
import com.example.dicedrop.presentation.common.LocalGameFeedback
import com.example.dicedrop.presentation.ui.theme.DiceDropTheme

// Промо-ролик для RuStore: игра по сценарию на весь экран, без панелей системы.
// Ролик начинается по касанию, чтобы успеть включить запись экрана
class TrailerActivity : ComponentActivity() {

    private lateinit var feedback: TrailerFeedback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // Рисуем и под вырезом камеры, иначе сверху записи остаётся чёрная полоса
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        feedback = TrailerFeedback(this)

        setContent {
            DiceDropTheme {
                CompositionLocalProvider(LocalGameFeedback provides feedback) {
                    TrailerContent()
                }
            }
        }
    }

    override fun onDestroy() {
        feedback.release()
        super.onDestroy()
    }
}

// Звуки игры без вибрации и без настроек: в ролике звук всегда включён.
// Файлы звуков называются как значения Sound в нижнем регистре
private class TrailerFeedback(context: Context) : GameFeedback {

    private val soundPool = SoundPool.Builder()
        .setMaxStreams(6)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    @SuppressLint("DiscouragedApi")
    private val soundIds = GameFeedback.Sound.entries.associateWith {
        soundPool.load(context, context.resources.getIdentifier(it.name.lowercase(), "raw", context.packageName), 1)
    }

    override fun play(sound: GameFeedback.Sound) {
        soundIds[sound]?.let { soundPool.play(it, 1f, 1f, 1, 0, 1f) }
    }

    override fun vibrate(haptic: GameFeedback.Haptic) = Unit

    fun release() = soundPool.release()
}

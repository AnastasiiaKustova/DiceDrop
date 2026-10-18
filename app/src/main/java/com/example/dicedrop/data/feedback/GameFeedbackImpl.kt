package com.example.dicedrop.data.feedback

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.dicedrop.R
import com.example.dicedrop.domain.feedback.GameFeedback
import com.example.dicedrop.domain.feedback.GameFeedback.Haptic
import com.example.dicedrop.domain.feedback.GameFeedback.Sound
import com.example.dicedrop.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class GameFeedbackImpl @Inject constructor(
    context: Context,
    settingsRepository: SettingsRepository
) : GameFeedback {

    // Читаем из любого потока, поэтому @Volatile
    @Volatile private var isSoundOn = true
    @Volatile private var isVibrationOn = true

    private val soundPool = SoundPool.Builder()
        .setMaxStreams(6)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    // Звуки грузятся асинхронно; пока звук не загружен, play() его просто пропускает
    private val soundIds: Map<Sound, Int> = Sound.entries.associateWith {
        soundPool.load(context, it.rawRes(), 1)
    }

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }?.takeIf { it.hasVibrator() }

    init {
        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            settingsRepository.gameSettingsFlow.collect {
                isSoundOn = it.isSoundOn
                isVibrationOn = it.isVibrationOn
            }
        }
    }

    override fun play(sound: Sound) {
        if (!isSoundOn) return
        val id = soundIds[sound] ?: return
        soundPool.play(id, 1f, 1f, sound.priority(), 0, 1f)
    }

    override fun vibrate(haptic: Haptic) {
        if (!isVibrationOn) return
        vibrator?.vibrate(haptic.effect())
    }

    private fun Sound.rawRes(): Int = when (this) {
        Sound.DICE_ROLL -> R.raw.dice_roll
        Sound.LINE_CLEAR -> R.raw.line_clear
        Sound.COMBO_X2 -> R.raw.combo_x2
        Sound.COMBO_X3 -> R.raw.combo_x3
        Sound.COMBO_X4 -> R.raw.combo_x4
        Sound.COMBO_X5 -> R.raw.combo_x5
        Sound.COIN -> R.raw.coin
        Sound.COINS_REWARD -> R.raw.coins_reward
        Sound.BONUS_BUY -> R.raw.bonus_buy
        Sound.ACHIEVEMENT -> R.raw.achievement
        Sound.NEW_RECORD -> R.raw.new_record
        Sound.GAME_OVER -> R.raw.game_over
        Sound.BUTTON_CLICK -> R.raw.button_click
        Sound.NOT_ENOUGH_COINS -> R.raw.not_enough_coins
        Sound.DAILY_GIFT -> R.raw.daily_gift
        Sound.BONUS_ROTATE -> R.raw.bonus_rotate
        Sound.BONUS_FREEZE -> R.raw.bonus_freeze
        Sound.BONUS_THAW -> R.raw.bonus_thaw
        Sound.BONUS_LUCKY_SHEET -> R.raw.bonus_lucky_sheet
        Sound.BONUS_LUCKY -> R.raw.bonus_lucky
        Sound.BONUS_DISCARD -> R.raw.bonus_discard
    }

    // Если потоков не хватает, щелчок кнопки уступает место важным звукам
    private fun Sound.priority(): Int = if (this == Sound.BUTTON_CLICK) 0 else 1

    private fun Haptic.effect(): VibrationEffect {
        // Готовые системные эффекты ощущаются аккуратнее, но есть только с Android 10
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            when (this) {
                Haptic.TICK -> return VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
                Haptic.PLACE -> return VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                Haptic.LINE -> return VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
                Haptic.BONUS -> return VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK)
                else -> Unit
            }
        }
        return when (this) {
            Haptic.TICK -> VibrationEffect.createOneShot(12, 70)
            Haptic.PLACE -> VibrationEffect.createOneShot(18, 120)
            Haptic.LINE -> VibrationEffect.createOneShot(35, 180)
            Haptic.COMBO -> VibrationEffect.createWaveform(
                longArrayOf(0, 35, 50, 45),
                intArrayOf(0, 180, 0, 230),
                -1
            )
            Haptic.BIG_COMBO -> VibrationEffect.createWaveform(
                longArrayOf(0, 40, 45, 40, 45, 70),
                intArrayOf(0, 200, 0, 230, 0, 255),
                -1
            )
            Haptic.BONUS -> VibrationEffect.createWaveform(
                longArrayOf(0, 20, 60, 25),
                intArrayOf(0, 140, 0, 170),
                -1
            )
            Haptic.REJECT -> VibrationEffect.createWaveform(
                longArrayOf(0, 30, 70, 30),
                intArrayOf(0, 150, 0, 150),
                -1
            )
            Haptic.CELEBRATE -> VibrationEffect.createWaveform(
                longArrayOf(0, 40, 70, 40, 70, 90),
                intArrayOf(0, 160, 0, 200, 0, 255),
                -1
            )
            Haptic.GAME_OVER -> VibrationEffect.createOneShot(280, 110)
        }
    }
}

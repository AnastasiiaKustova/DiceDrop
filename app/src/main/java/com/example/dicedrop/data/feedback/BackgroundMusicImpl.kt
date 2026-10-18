package com.example.dicedrop.data.feedback

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.example.dicedrop.R
import com.example.dicedrop.domain.feedback.BackgroundMusic
import com.example.dicedrop.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject

// Музыка подаётся потоком по кругу: MediaPlayer с isLooping на части телефонов (Samsung)
// гасит конец файла и делает паузу перед повтором, а здесь стыка нет вовсе.
// Управление (update) идёт из главного потока, отдельный поток только пишет звук в AudioTrack
class BackgroundMusicImpl @Inject constructor(
    context: Context,
    settingsRepository: SettingsRepository
) : BackgroundMusic {

    private val appContext = context.applicationContext
    private var track: AudioTrack? = null

    // Пока настройки не прочитаны, музыку не включаем: иначе у того, кто её выключил, она мелькнёт при запуске
    private var settingsLoaded = false
    private var isMusicOn = false
    private var isForeground = false
    private var isPaused = false

    init {
        CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {
            isMusicOn = settingsRepository.getSettings().isMusicOn
            settingsLoaded = true
            update()
            settingsRepository.gameSettingsFlow.collect {
                isMusicOn = it.isMusicOn
                update()
            }
        }
    }

    override fun setForeground(isForeground: Boolean) {
        this.isForeground = isForeground
        update()
    }

    override fun setPaused(isPaused: Boolean) {
        this.isPaused = isPaused
        update()
    }

    private fun update() {
        val shouldPlay = settingsLoaded && isMusicOn && isForeground && !isPaused
        if (shouldPlay) {
            val current = track ?: createTrack()?.also { track = it } ?: return
            if (current.playState != AudioTrack.PLAYSTATE_PLAYING) current.play()
        } else {
            // На паузе поток записи упирается в заполненный буфер и ждёт, пока музыку снова включат
            track?.takeIf { it.playState == AudioTrack.PLAYSTATE_PLAYING }?.pause()
        }
    }

    private fun createTrack(): AudioTrack? {
        val wav = readWav(R.raw.music_playful) ?: return null
        val format = AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setSampleRate(wav.sampleRate)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()
        val minBuffer = AudioTrack.getMinBufferSize(
            wav.sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(format)
            .setBufferSizeInBytes(minBuffer * 4)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()
        // Музыка - фон: тише звуков, чтобы они были хорошо слышны поверх неё
        audioTrack.setVolume(MUSIC_VOLUME)

        Thread({ writeLoop(audioTrack, wav.samples) }, "BackgroundMusic").apply {
            isDaemon = true
            start()
        }
        return audioTrack
    }

    // Пишем отсчёты по кругу: после последнего сразу идёт первый
    private fun writeLoop(audioTrack: AudioTrack, samples: ShortArray) {
        var position = 0
        while (true) {
            val count = minOf(CHUNK, samples.size - position)
            val written = audioTrack.write(samples, position, count)
            if (written < 0) return
            position = (position + written) % samples.size
        }
    }

    private class Wav(val sampleRate: Int, val samples: ShortArray)

    // WAV из генератора: моно, 16 бит; ищем блоки "fmt " и "data"
    private fun readWav(resId: Int): Wav? {
        val bytes = appContext.resources.openRawResource(resId).use { it.readBytes() }
        val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
        var sampleRate = 0
        var offset = 12
        while (offset + 8 <= bytes.size) {
            val id = String(bytes, offset, 4, Charsets.US_ASCII)
            val size = buffer.getInt(offset + 4)
            val body = offset + 8
            when (id) {
                "fmt " -> sampleRate = buffer.getInt(body + 4)
                "data" -> {
                    val samples = ShortArray(minOf(size, bytes.size - body) / 2)
                    buffer.position(body)
                    buffer.asShortBuffer().get(samples)
                    return if (sampleRate > 0) Wav(sampleRate, samples) else null
                }
            }
            offset = body + size + (size and 1)
        }
        return null
    }

    private companion object {
        const val MUSIC_VOLUME = 0.9f
        const val CHUNK = 4096
    }
}

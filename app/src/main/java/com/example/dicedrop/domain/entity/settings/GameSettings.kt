package com.example.dicedrop.domain.entity.settings

import java.util.Locale

// Значения по умолчанию у всех полей нужны Gson: тогда он создает объект через конструктор
// без параметров, и поля, которых нет в старом сохранении, получают дефолт, а не null
data class GameSettings(
    val isSoundOn: Boolean = true,
    val isVibrationOn: Boolean = true,
    val isMusicOn: Boolean = true,
    val language: String = Locale.getDefault().language,
    // Обучение показывается при первом запуске, пока игрок его не закроет
    val isTutorialShown: Boolean = false,
    // Сколько раз показано окно «Ходов нет»: первые разы - окно, потом только подсказка
    val stuckDialogsShown: Int = 0,
){
    companion object{
        fun default() = GameSettings()
    }
}

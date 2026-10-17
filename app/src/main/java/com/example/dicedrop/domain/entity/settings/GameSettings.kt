package com.example.dicedrop.domain.entity.settings

import java.util.Locale

// Значения по умолчанию у всех полей нужны Gson: тогда он создает объект через конструктор
// без параметров, и поля, которых нет в старом сохранении, получают дефолт, а не null
data class GameSettings(
    val musicVolume: Float = 0f,
    val language: String = Locale.getDefault().language,
    val isAdsOff: Boolean = false,
){
    companion object{
        fun default() = GameSettings()
    }
}

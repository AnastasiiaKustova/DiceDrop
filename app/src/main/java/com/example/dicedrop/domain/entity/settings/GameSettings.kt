package com.example.dicedrop.domain.entity.settings

import java.util.Locale

data class GameSettings(
    val musicVolume: Float = 0f,
    val language: String,
    val useAutoRoll: Boolean,
    val isAdsOff: Boolean,
){
    companion object{
        fun default() = GameSettings(
            musicVolume = 0f,
            language = Locale.getDefault().language,
            useAutoRoll = false,
            isAdsOff = false
        )
    }
}

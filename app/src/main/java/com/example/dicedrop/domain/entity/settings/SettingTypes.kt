package com.example.dicedrop.domain.entity.settings

sealed interface SettingTypes{
    data class Sound(
        val isOn: Boolean
    ): SettingTypes

    data class Vibration(
        val isOn: Boolean
    ): SettingTypes

    data class Music(
        val isOn: Boolean
    ): SettingTypes

    data class Language(
        val language: String
    ): SettingTypes
}

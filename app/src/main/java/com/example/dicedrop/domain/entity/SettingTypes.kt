package com.example.dicedrop.domain.entity

sealed interface SettingTypes{
    data class UseAutoRoll(
        val checked: Boolean
    ): SettingTypes

    data class MusicVolume(
        val value: Float
    ): SettingTypes

    data class Language(
        val language: String,
        val valueList: List<String>
    ): SettingTypes
}

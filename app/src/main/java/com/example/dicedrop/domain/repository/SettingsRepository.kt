package com.example.dicedrop.domain.repository

import com.example.dicedrop.domain.entity.settings.GameSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    val gameSettingsFlow: Flow<GameSettings>

    suspend fun saveSettings(settings: GameSettings)

    suspend fun getSettings(): GameSettings

}
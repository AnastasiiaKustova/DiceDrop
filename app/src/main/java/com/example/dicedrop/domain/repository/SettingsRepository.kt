package com.example.dicedrop.domain.repository

import com.example.dicedrop.domain.entity.GameSettings
import kotlinx.coroutines.flow.Flow
import org.intellij.lang.annotations.Language

interface SettingsRepository {

    val gameSettingsFlow: Flow<GameSettings>

    suspend fun saveSettings(settings: GameSettings)

    suspend fun getSettings(): GameSettings

}
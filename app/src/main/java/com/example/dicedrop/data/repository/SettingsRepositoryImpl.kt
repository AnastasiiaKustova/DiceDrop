package com.example.dicedrop.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.dicedrop.domain.entity.settings.GameSettings
import com.example.dicedrop.domain.repository.SettingsRepository
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val gson: Gson
) : SettingsRepository {

    private object Keys { val SETTINGS = stringPreferencesKey("settings_json") }

    private val _settings = MutableStateFlow(GameSettings.default())
    override val gameSettingsFlow: Flow<GameSettings> = _settings.asStateFlow()

    init {
        // Подписываемся на DataStore, чтобы держать актуальные данные
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.data.collect { prefs ->
                val json = prefs[Keys.SETTINGS]
                val parsed = json?.let { gson.fromJson(it, GameSettings::class.java) }
                _settings.value = parsed ?: GameSettings.default()
            }
        }
    }

    override suspend fun saveSettings(settings: GameSettings) {
        _settings.value = settings
        dataStore.edit { prefs ->
            prefs[Keys.SETTINGS] = gson.toJson(settings)
        }
    }

    override suspend fun getSettings(): GameSettings = _settings.value
}
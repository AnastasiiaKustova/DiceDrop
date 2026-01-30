package com.example.dicedrop.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.dicedrop.data.mapper.fromJsonOrDefault
import com.example.dicedrop.domain.entity.GameStatisticItem
import com.example.dicedrop.domain.entity.GameStatisticItem.Companion.default
import com.example.dicedrop.domain.repository.GameRepository
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val gson: Gson
) : GameRepository {

    private object Keys { val GAME_STAT = stringPreferencesKey("game_statistic_json") }

    private val repositoryJob = SupervisorJob()
    private val repositoryScope = CoroutineScope(Dispatchers.IO + repositoryJob)

    private val _gameState = MutableStateFlow(default())
    override val gameStatisticFlow: Flow<GameStatisticItem> = _gameState.asStateFlow()

    init {
        repositoryScope.launch {
            _gameState.value = loadGame()
        }
    }

    override val coinsValueFlow: Flow<Int> = _gameState
        .map { it.coins }
        .stateIn(repositoryScope, SharingStarted.Lazily, 0)

    override suspend fun getCurrentGame(): GameStatisticItem = gameStatisticFlow.first()

    override suspend fun saveGame(gameStatistic: GameStatisticItem) {
        _gameState.value = gameStatistic
        dataStore.edit { prefs ->
            prefs[Keys.GAME_STAT] = gson.toJson(gameStatistic)
        }
    }

    override suspend fun loadGame(): GameStatisticItem {
        val saved = dataStore.data.firstOrNull()?.let { prefs ->
            gson.fromJsonOrDefault(prefs[Keys.GAME_STAT], default())
        } ?: default()

        _gameState.value = saved

        return saved
    }

    override suspend fun updateGameState(game: GameStatisticItem) {
        _gameState.value = game
    }
}
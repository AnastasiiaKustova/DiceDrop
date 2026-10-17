package com.example.dicedrop.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.dicedrop.data.mapper.fromJsonOrDefault
import com.example.dicedrop.domain.entity.GameStatisticItem
import com.example.dicedrop.domain.entity.GameStatisticItem.Companion.default
import com.example.dicedrop.domain.entity.TrayState
import com.example.dicedrop.domain.repository.GameRepository
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val gson: Gson
) : GameRepository {

    private object Keys { val GAME_STAT = stringPreferencesKey("game_statistic_json_v2") }

    private val repositoryJob = SupervisorJob()
    private val repositoryScope = CoroutineScope(Dispatchers.IO + repositoryJob)

    private val _gameState = MutableStateFlow(default())

    @Volatile
    private var lastSaved: GameStatisticItem? = null

    // Пока сохранение не прочитано с диска, наружу отдавать ничего нельзя,
    // иначе дефолтное состояние перезапишет реальный прогресс
    private val loadJob = repositoryScope.launch {
        _gameState.value = readGame()
        startAutoSave()
    }

    override val gameStatisticFlow: Flow<GameStatisticItem> = flow {
        loadJob.join()
        emitAll(_gameState)
    }

    override val coinsValueFlow: Flow<Int> = gameStatisticFlow
        .map { it.coins }

    override suspend fun getCurrentGame(): GameStatisticItem {
        loadJob.join()
        return _gameState.value
    }

    override suspend fun saveGame(gameStatistic: GameStatisticItem) {
        loadJob.join()
        _gameState.value = gameStatistic
        writeGame(gameStatistic)
    }

    override suspend fun loadGame(): GameStatisticItem = getCurrentGame()

    override suspend fun updateGameState(game: GameStatisticItem) {
        loadJob.join()
        _gameState.value = game
    }

    // Пишет на диск после паузы в изменениях, а не на каждое изменение
    @OptIn(FlowPreview::class)
    private fun startAutoSave() {
        repositoryScope.launch {
            _gameState
                .drop(1)
                .debounce(AUTO_SAVE_DELAY_MS)
                .filter { it !== lastSaved }
                .collect { writeGame(it) }
        }
    }

    private suspend fun writeGame(gameStatistic: GameStatisticItem) {
        lastSaved = gameStatistic
        dataStore.edit { prefs ->
            prefs[Keys.GAME_STAT] = gson.toJson(gameStatistic)
        }
    }

    private suspend fun readGame(): GameStatisticItem {
        val saved = dataStore.data.firstOrNull()?.let { prefs ->
            gson.fromJsonOrDefault(prefs[Keys.GAME_STAT], default())
        } ?: default()

        return saved.copy(
            bonuses = default().bonuses.map { def ->
                val count = saved.bonuses.firstOrNull {
                    runCatching { it.type }.getOrNull() == def.type
                }?.count ?: 0
                def.copy(count = count)
            },
            tray = saved.tray ?: TrayState()
        )
    }

    private companion object {
        const val AUTO_SAVE_DELAY_MS = 500L
    }
}

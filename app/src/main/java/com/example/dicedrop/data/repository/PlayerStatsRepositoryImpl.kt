package com.example.dicedrop.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.dicedrop.data.mapper.fromJsonOrDefault
import com.example.dicedrop.domain.entity.statistic.PlayerStat
import com.example.dicedrop.domain.entity.statistic.PlayerStats
import com.example.dicedrop.domain.repository.PlayerStatsRepository
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.dicedrop.domain.entity.statistic.PlayerStats.Companion.get


class PlayerStatsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val gson: Gson
) : PlayerStatsRepository {

    private object Keys {
        val STATS = stringPreferencesKey("player_stats_json")
    }

    private val _statsFlow = MutableStateFlow(PlayerStats())
    override val statsFlow: Flow<PlayerStats> = _statsFlow.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        scope.launch {
            _statsFlow.value = load()
        }
    }

    override suspend fun increment(stat: PlayerStat, amount: Long) {
        val current = _statsFlow.value.counters.toMutableMap()
        current[stat] = (current[stat] ?: 0L) + amount
        save(PlayerStats(current))
    }

    override suspend fun get(stat: PlayerStat): Long {
        return _statsFlow.value.get(stat)
    }

    override suspend fun reset(stat: PlayerStat) {
        val current = _statsFlow.value.counters.toMutableMap()
        current.remove(stat)
        save(PlayerStats(current))
    }

    override suspend fun resetAll() {
        save(PlayerStats())
    }

    private suspend fun save(stats: PlayerStats) {
        _statsFlow.value = stats
        dataStore.edit {
            it[Keys.STATS] = gson.toJson(stats)
        }
    }

    private suspend fun load(): PlayerStats {
        val json = dataStore.data.firstOrNull()?.get(Keys.STATS)
        return gson.fromJsonOrDefault(json, PlayerStats())
    }
}
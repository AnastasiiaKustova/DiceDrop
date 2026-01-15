package com.example.dicedrop.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.dicedrop.data.mapper.fromJsonOrDefault
import com.example.dicedrop.domain.catalog.AchievementsCatalog
import com.example.dicedrop.domain.entity.Achievement
import com.example.dicedrop.domain.entity.AchievementProgress
import com.example.dicedrop.domain.repository.AchievementRepository
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

class AchievementRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val gson: Gson
) : AchievementRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private object Keys {
        val ACHIEVEMENTS = stringPreferencesKey("game_achievements_json")
    }

    private val _achievementState: MutableStateFlow<List<AchievementProgress>> = MutableStateFlow(emptyList())
    override val achievementState: Flow<List<AchievementProgress>> = _achievementState.asStateFlow()

    init {
        scope.launch {
            _achievementState.value = loadProgress()
        }
    }

    override suspend fun getCurrentAchievementProgress(): List<AchievementProgress> = achievementState.first()

    override suspend fun achievementRewardReceived(achievement: Achievement) {
        val current = getCurrentAchievementProgress()
        val updated = if (current.any { it.id == achievement.id }) {
            current.map {
                if (it.id == achievement.id) it.copy(isRewardClaimed = true) else it
            }
        } else {
            current + AchievementProgress(id = achievement.id, isRewardClaimed = true)
        }

        saveAchievementProgress(updated)
    }

    override suspend fun getAchievements(): List<Achievement> {
        val all = AchievementsCatalog.all
        val progress = getCurrentAchievementProgress()

        return all.map { achievement ->
            val state = progress.find { it.id == achievement.id }
            achievement.copy(
                isRewardClaimed = state?.isRewardClaimed ?: false
            )
        }
    }

    override suspend fun saveAchievementProgress(list: List<AchievementProgress>) {
        _achievementState.value = list
        val json = gson.toJson(list)
        dataStore.edit { prefs ->
            prefs[Keys.ACHIEVEMENTS] = json
        }
    }

    private suspend fun loadProgress(): List<AchievementProgress> {
        val json = dataStore.data.firstOrNull()?.get(Keys.ACHIEVEMENTS)
        return gson.fromJsonOrDefault<List<AchievementProgress>>(json, emptyList())
    }
}
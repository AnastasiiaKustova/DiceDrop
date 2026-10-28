package com.example.dicedrop.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.dicedrop.data.mapper.fromJsonOrDefault
import com.example.dicedrop.domain.entity.DailyReward
import com.example.dicedrop.domain.entity.ShopItem
import com.example.dicedrop.domain.repository.ShopRepository
import com.example.dicedrop.domain.useCases.DateHelper
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ShopRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val gson: Gson
) : ShopRepository {

    private object Keys {
        val REWARD = stringPreferencesKey("player_daily_reward_json")
    }

    private val _rewardFlow = MutableStateFlow(DailyReward.default())

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val loadJob = scope.launch {
        _rewardFlow.value = load()
    }

    override val rewardFlow: Flow<DailyReward> = flow {
        loadJob.join()
        emitAll(_rewardFlow)
    }

    private suspend fun save(data: DailyReward) {
        _rewardFlow.value = data
        dataStore.edit {
            it[Keys.REWARD] = gson.toJson(data)
        }
    }

    private suspend fun load(): DailyReward {
        val json = dataStore.data.firstOrNull()?.get(Keys.REWARD)
        return gson.fromJsonOrDefault(json, DailyReward.default())
    }

    override suspend fun isDailyRewardReceived(date: String): Boolean {
        loadJob.join()
        return _rewardFlow.value.lastDate == date
    }

    override suspend fun getDailyReward(): Int {
        loadJob.join()
        val data = _rewardFlow.value
        // Серия продолжается, только если вчера подарок тоже забрали, иначе начинается заново
        val streak = if (data.lastDate == DateHelper.getYesterdayDate()) data.dayStreak + 1 else 1

        save(
            data.copy(
                lastDate = DateHelper.getCurrentDate(),
                dayStreak = streak
            )
        )
        return streak
    }

    override suspend fun registerAdBonus(): Boolean {
        loadJob.join()
        val data = _rewardFlow.value
        if (data.adBonusesLeft <= 0) return false
        val today = DateHelper.getCurrentDate()
        save(
            data.copy(
                adBonusDate = today,
                adBonusCount = if (data.adBonusDate == today) data.adBonusCount + 1 else 1
            )
        )
        return true
    }

    override suspend fun getShopItems(): List<ShopItem> {
        return emptyList()
    }
}
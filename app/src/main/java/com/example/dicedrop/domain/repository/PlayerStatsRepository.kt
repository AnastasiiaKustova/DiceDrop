package com.example.dicedrop.domain.repository

import com.example.dicedrop.domain.entity.statistic.PlayerStat
import com.example.dicedrop.domain.entity.statistic.PlayerStats
import kotlinx.coroutines.flow.Flow

interface PlayerStatsRepository {

    val statsFlow: Flow<PlayerStats>

    suspend fun increment(stat: PlayerStat, amount: Long = 1)

    suspend fun updateMax(stat: PlayerStat, value: Long)

    suspend fun get(stat: PlayerStat): Long

    suspend fun reset(stat: PlayerStat)

    suspend fun resetAll()
}
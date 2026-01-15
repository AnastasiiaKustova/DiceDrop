package com.example.dicedrop.domain.repository

import com.example.dicedrop.domain.entity.Achievement
import com.example.dicedrop.domain.entity.AchievementProgress
import kotlinx.coroutines.flow.Flow

interface AchievementRepository {

    val achievementState: Flow<List<AchievementProgress>>

    suspend fun getCurrentAchievementProgress(): List<AchievementProgress>

    suspend fun achievementRewardReceived(achievement: Achievement)

    suspend fun getAchievements(): List<Achievement>

    suspend fun saveAchievementProgress(list: List<AchievementProgress>)
}
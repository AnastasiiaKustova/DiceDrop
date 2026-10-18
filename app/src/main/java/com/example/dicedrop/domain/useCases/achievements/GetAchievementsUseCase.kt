package com.example.dicedrop.domain.useCases.achievements

import com.example.dicedrop.domain.entity.achievement.Achievement
import com.example.dicedrop.domain.entity.achievement.toStat
import com.example.dicedrop.domain.entity.statistic.PlayerStats.Companion.get
import com.example.dicedrop.domain.repository.AchievementRepository
import com.example.dicedrop.domain.repository.PlayerStatsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetAchievementsUseCase @Inject constructor(
private val achievementRepository: AchievementRepository,
private val statsRepository: PlayerStatsRepository
) {
    suspend operator fun invoke(): List<Achievement> {
        val achievements = achievementRepository.getAchievements()
        val stats = statsRepository.statsFlow.first()

        return achievements.map { achievement ->
            val statValue = stats.get(achievement.type.toStat())
            achievement.copy(totalCount = statValue.toInt())
        }
    }
}
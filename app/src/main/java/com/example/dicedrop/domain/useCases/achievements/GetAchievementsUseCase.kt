package com.example.dicedrop.domain.useCases.achievements

import com.example.dicedrop.domain.entity.achievement.Achievement
import com.example.dicedrop.domain.entity.achievement.AchievementType
import com.example.dicedrop.domain.entity.statistic.PlayerStat
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
        val stats = statsRepository.statsFlow.first() // актуальная статистика игрока

        return achievements.map { achievement ->
            val statValue = when (achievement.type) {
                AchievementType.ROLL_TIMES -> stats.get(PlayerStat.ROLL_COUNT)
                AchievementType.CLEAR_ROWS -> stats.get(PlayerStat.LINES_CLEARED)
            }
            achievement.copy(totalCount = statValue.toInt())
        }
    }
}
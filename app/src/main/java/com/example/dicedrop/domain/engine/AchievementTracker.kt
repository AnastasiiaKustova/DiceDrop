package com.example.dicedrop.domain.engine

import com.example.dicedrop.domain.entity.achievement.Achievement
import com.example.dicedrop.domain.entity.achievement.AchievementType
import com.example.dicedrop.domain.entity.statistic.AchievementUnlockedEvent
import com.example.dicedrop.domain.entity.statistic.PlayerStat
import com.example.dicedrop.domain.entity.statistic.PlayerStats
import com.example.dicedrop.domain.entity.statistic.PlayerStats.Companion.get
import com.example.dicedrop.domain.repository.AchievementRepository
import com.example.dicedrop.domain.repository.PlayerStatsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AchievementTracker @Inject constructor(
    private val statsRepository: PlayerStatsRepository,
    private val achievementRepository: AchievementRepository
) {

    private val _events = MutableSharedFlow<AchievementUnlockedEvent>()
    val events: SharedFlow<AchievementUnlockedEvent> = _events

    suspend fun onRoll() {
        check(AchievementType.ROLL_TIMES)
    }

    suspend fun onClearRows() {
        check(AchievementType.CLEAR_ROWS)
    }

    suspend fun onBombUsed() {
        //check(AchievementType.BOMB_USED)
    }

    private suspend fun check(type: AchievementType) {
        val stats = statsRepository.statsFlow.first()
        val achievements = achievementRepository.getAchievements()

        achievements
            .filter { it.type == type && !it.isRewardClaimed }
            .forEach { achievement ->
                if (stats.meets(achievement)) {
                    _events.emit(AchievementUnlockedEvent(achievement))
                }
            }
    }

    private fun PlayerStats.meets(achievement: Achievement): Boolean {
        return when (achievement.type) {
            AchievementType.ROLL_TIMES -> this.get(PlayerStat.ROLL_COUNT) >= achievement.requiredCount
            AchievementType.CLEAR_ROWS -> this.get(PlayerStat.LINES_CLEARED) >= achievement.requiredCount
        }
    }
}
package com.example.dicedrop.domain.useCases.achievements

import com.example.dicedrop.core.IllegalException
import com.example.dicedrop.domain.engine.AchievementTracker
import com.example.dicedrop.domain.entity.achievement.AchievementType
import com.example.dicedrop.domain.entity.statistic.PlayerStat
import com.example.dicedrop.domain.repository.AchievementRepository
import com.example.dicedrop.domain.repository.GameRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class GetAchievementRewardUseCase@Inject constructor(
    private val repositoryAchievement: AchievementRepository,
    private val repositoryGame: GameRepository,
    private val getAchievementsUseCase: GetAchievementsUseCase,
    private val achievementTracker: AchievementTracker
) {
    suspend operator fun invoke(achievementId: String): Boolean = mutex.withLock {
        val achievementList = getAchievementsUseCase()
        val achievement = achievementList.firstOrNull { it.id == achievementId }

        if (achievement == null) throw IllegalException("Achievement with id $achievementId not found")

        // Повторный тап или невыполненное условие - награду не выдаем
        if (achievement.isRewardClaimed) return@withLock false
        if (achievement.totalCount < achievement.requiredCount) return@withLock false

        val currentGame = repositoryGame.getCurrentGame()

        val updatedBonuses = if (achievement.additionalRewards.isNotEmpty()) {
            currentGame.bonuses.map { bonus ->
                achievement.additionalRewards.find { it.bonusType == bonus.type }
                    ?.let { additionalReward ->
                        bonus.copy(count = bonus.count + additionalReward.amount)
                    } ?: bonus
            }
        } else {
            currentGame.bonuses
        }

        val updatedGame = currentGame.copy(
            bonuses = updatedBonuses,
            coins = currentGame.coins + achievement.reward
        )

        repositoryGame.saveGame(updatedGame)
        repositoryAchievement.achievementRewardReceived(achievement)

        // «Чемпион» считает забранные награды всех остальных достижений
        val claimed = repositoryAchievement.getAchievements()
            .count { it.isRewardClaimed && it.type != AchievementType.ACHIEVEMENTS_CLAIMED }
        achievementTracker.updateMax(PlayerStat.ACHIEVEMENTS_CLAIMED, claimed.toLong())
        true
    }

    private companion object {
        // Общий на все экземпляры, чтобы двойной тап не выдал награду дважды
        val mutex = Mutex()
    }
}

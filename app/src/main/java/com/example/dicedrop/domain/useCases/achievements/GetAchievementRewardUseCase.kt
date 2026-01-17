package com.example.dicedrop.domain.useCases.achievements

import com.example.dicedrop.core.IllegalException
import com.example.dicedrop.domain.repository.AchievementRepository
import com.example.dicedrop.domain.repository.GameRepository
import javax.inject.Inject

class GetAchievementRewardUseCase@Inject constructor(
    private val repositoryAchievement: AchievementRepository,
    private val repositoryGame: GameRepository
) {
    suspend operator fun invoke(achievementId: String){
        val currentGame = repositoryGame.getCurrentGame()
        val achievementList = repositoryAchievement.getAchievements()
        val achievement = achievementList.firstOrNull { it.id == achievementId }

        if (achievement == null) throw IllegalException("Achievement with id $achievementId not found")

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
        //repositoryAchievement.achievementRewardReceived(achievement)
    }
}
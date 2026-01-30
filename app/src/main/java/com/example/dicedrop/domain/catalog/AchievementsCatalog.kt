package com.example.dicedrop.domain.catalog

import com.example.dicedrop.domain.entity.achievement.Achievement
import com.example.dicedrop.domain.entity.achievement.AchievementReward
import com.example.dicedrop.domain.entity.achievement.AchievementType
import com.example.dicedrop.domain.entity.BonusType

object AchievementsCatalog {
    val all: List<Achievement> = listOf(
        Achievement(
            id = "roll_50_times",
            title = "Just the Beginning",
            description = "Roll the dice 50 times",
            type = AchievementType.ROLL_TIMES,
            requiredCount = 50,
            reward = 5,
        ),
        Achievement(
            id = "roll_100_times",
            title = "Getting the Hang of It",
            description = "Roll the dice 100 times",
            type = AchievementType.ROLL_TIMES,
            requiredCount = 100,
            reward = 10,
        ),
        Achievement(
            id = "roll_500_times",
            title = "On a Roll",
            description = "Roll the dice 500 times",
            type = AchievementType.ROLL_TIMES,
            requiredCount = 500,
            reward = 15,
        ),
        Achievement(
            id = "roll_1000_times",
            title = "Dice Legend",
            description = "Roll the dice 1000 times",
            type = AchievementType.ROLL_TIMES,
            requiredCount = 1000,
            reward = 20,
            additionalRewards = listOf(
                AchievementReward(
                    bonusType = BonusType.HAMMER,
                    amount = 1
                )
            )
        ),
        Achievement(
            id = "clear_50_rows",
            title = "Just the Beginning",
            description = "Clear the rows 50 times",
            type = AchievementType.CLEAR_ROWS,
            requiredCount = 50,
            reward = 5,
        ),
    )
}
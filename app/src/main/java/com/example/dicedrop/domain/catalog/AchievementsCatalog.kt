package com.example.dicedrop.domain.catalog

import com.example.dicedrop.R
import com.example.dicedrop.domain.entity.achievement.Achievement
import com.example.dicedrop.domain.entity.achievement.AchievementReward
import com.example.dicedrop.domain.entity.achievement.AchievementType
import com.example.dicedrop.domain.entity.BonusType

object AchievementsCatalog {
    val all: List<Achievement> = listOf(
        Achievement(
            id = "roll_50_times",
            titleId = R.string.just_the_beginning,
            descriptionId = R.string.roll_the_dice_50_times,
            type = AchievementType.ROLL_TIMES,
            requiredCount = 50,
            reward = 5,
        ),
        Achievement(
            id = "roll_100_times",
            titleId = R.string.getting_the_hang_of_it,
            descriptionId = R.string.roll_the_dice_100_times,
            type = AchievementType.ROLL_TIMES,
            requiredCount = 100,
            reward = 10,
        ),
        Achievement(
            id = "roll_500_times",
            titleId = R.string.on_a_roll,
            descriptionId = R.string.roll_the_dice_500_times,
            type = AchievementType.ROLL_TIMES,
            requiredCount = 500,
            reward = 15,
        ),
        Achievement(
            id = "roll_1000_times",
            titleId = R.string.dice_legend,
            descriptionId = R.string.roll_the_dice_1000_times,
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
            titleId = R.string.just_the_beginning,
            descriptionId = R.string.clear_the_rows_50_times,
            type = AchievementType.CLEAR_ROWS,
            requiredCount = 50,
            reward = 5,
        ),
    )
}
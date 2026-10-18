package com.example.dicedrop.domain.catalog

import com.example.dicedrop.R
import com.example.dicedrop.domain.entity.achievement.Achievement
import com.example.dicedrop.domain.entity.achievement.AchievementReward
import com.example.dicedrop.domain.entity.achievement.AchievementType
import com.example.dicedrop.domain.entity.BonusType

// id не менять: по ним хранится, какие награды игрок уже забрал
object AchievementsCatalog {
    val all: List<Achievement> = listOf(
        // Броски
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
                    bonusType = BonusType.FREEZE,
                    amount = 1
                )
            )
        ),

        // Очищенные линии
        Achievement(
            id = "clear_10_rows",
            titleId = R.string.first_sweep,
            descriptionId = R.string.clear_10_lines,
            type = AchievementType.CLEAR_ROWS,
            requiredCount = 10,
            reward = 5,
        ),
        Achievement(
            id = "clear_50_rows",
            titleId = R.string.cleaner,
            descriptionId = R.string.clear_the_rows_50_times,
            type = AchievementType.CLEAR_ROWS,
            requiredCount = 50,
            reward = 10,
        ),
        Achievement(
            id = "clear_250_rows",
            titleId = R.string.cleanup_master,
            descriptionId = R.string.clear_250_lines,
            type = AchievementType.CLEAR_ROWS,
            requiredCount = 250,
            reward = 20,
            additionalRewards = listOf(
                AchievementReward(
                    bonusType = BonusType.FREEZE,
                    amount = 1
                )
            )
        ),

        // Линии за один ход
        Achievement(
            id = "lines_at_once_2",
            titleId = R.string.double_clear,
            descriptionId = R.string.clear_2_lines_at_once,
            type = AchievementType.LINES_AT_ONCE,
            requiredCount = 2,
            reward = 5,
        ),
        Achievement(
            id = "lines_at_once_3",
            titleId = R.string.hat_trick,
            descriptionId = R.string.clear_3_lines_at_once,
            type = AchievementType.LINES_AT_ONCE,
            requiredCount = 3,
            reward = 15,
            additionalRewards = listOf(
                AchievementReward(
                    bonusType = BonusType.LUCKY,
                    amount = 1
                )
            )
        ),

        // Комбо
        Achievement(
            id = "combo_5",
            titleId = R.string.combo_5_title,
            descriptionId = R.string.combo_5_description,
            type = AchievementType.COMBO,
            requiredCount = 5,
            reward = 10,
        ),
        Achievement(
            id = "combo_10",
            titleId = R.string.combo_10_title,
            descriptionId = R.string.combo_10_description,
            type = AchievementType.COMBO,
            requiredCount = 10,
            reward = 20,
            additionalRewards = listOf(
                AchievementReward(
                    bonusType = BonusType.ROTATE,
                    amount = 1
                )
            )
        ),

        // Полная очистка поля
        Achievement(
            id = "board_clear_1",
            titleId = R.string.clean_slate,
            descriptionId = R.string.clear_whole_board,
            type = AchievementType.BOARD_CLEARED,
            requiredCount = 1,
            reward = 15,
        ),
        Achievement(
            id = "board_clear_5",
            titleId = R.string.spring_cleaning,
            descriptionId = R.string.clear_whole_board_5_times,
            type = AchievementType.BOARD_CLEARED,
            requiredCount = 5,
            reward = 25,
            additionalRewards = listOf(
                AchievementReward(
                    bonusType = BonusType.FREEZE,
                    amount = 1
                )
            )
        ),

        // Блок 6×6 (площадь 36)
        Achievement(
            id = "big_block",
            titleId = R.string.giant,
            descriptionId = R.string.place_block_6x6,
            type = AchievementType.BIG_BLOCK,
            requiredCount = 36,
            reward = 10,
        ),

        // Очки за партию
        Achievement(
            id = "score_1000",
            titleId = R.string.score_1000_title,
            descriptionId = R.string.score_1000_description,
            type = AchievementType.GAME_SCORE,
            requiredCount = 1000,
            reward = 10,
        ),
        Achievement(
            id = "score_5000",
            titleId = R.string.score_5000_title,
            descriptionId = R.string.score_5000_description,
            type = AchievementType.GAME_SCORE,
            requiredCount = 5000,
            reward = 20,
            additionalRewards = listOf(
                AchievementReward(
                    bonusType = BonusType.DRAFT,
                    amount = 1
                )
            )
        ),

        // Сыгранные партии
        Achievement(
            id = "games_25",
            titleId = R.string.regular,
            descriptionId = R.string.play_25_games,
            type = AchievementType.GAMES_PLAYED,
            requiredCount = 25,
            reward = 10,
        ),

        // Использование бонусов
        Achievement(
            id = "use_rotate_10",
            titleId = R.string.acrobat,
            descriptionId = R.string.use_rotate_10_times,
            type = AchievementType.USE_ROTATE,
            requiredCount = 10,
            reward = 10,
        ),
        Achievement(
            id = "use_freeze_10",
            titleId = R.string.frost_master,
            descriptionId = R.string.use_freeze_10_times,
            type = AchievementType.USE_FREEZE,
            requiredCount = 10,
            reward = 10,
        ),
        Achievement(
            id = "use_lucky_10",
            titleId = R.string.lucky_one,
            descriptionId = R.string.use_lucky_10_times,
            type = AchievementType.USE_LUCKY,
            requiredCount = 10,
            reward = 10,
        ),
        Achievement(
            id = "use_draft_10",
            titleId = R.string.third_wheel,
            descriptionId = R.string.use_draft_10_times,
            type = AchievementType.USE_DRAFT,
            requiredCount = 10,
            reward = 10,
        ),
    )
}

package com.example.dicedrop.domain.catalog

import com.example.dicedrop.R
import com.example.dicedrop.domain.entity.achievement.Achievement
import com.example.dicedrop.domain.entity.achievement.AchievementReward
import com.example.dicedrop.domain.entity.achievement.AchievementType
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.presentation.game.ui.bottompanel.getIconResId

// id не менять: по ним хранится, какие награды игрок уже забрал.
// Порядок здесь - порядок на экране достижений: по возрастанию награды,
// бонусы считаются по цене магазина (Перевернуть 5, Заморозка и Третий кубик 10, Счастливый 15)
object AchievementsCatalog {
    val all: List<Achievement> = listOf(
        // Награда 10
        Achievement(
            id = "double_roll",
            titleId = R.string.ach_double_roll_title,
            descriptionId = R.string.ach_double_roll_desc,
            iconId = R.drawable.ach_double_roll,
            type = AchievementType.DOUBLE_ROLLED,
            requiredCount = 1,
            reward = 10
        ),
        Achievement(
            id = "clear_10_rows",
            titleId = R.string.ach_clear_10_rows_title,
            descriptionId = R.string.ach_clear_10_rows_desc,
            iconId = R.drawable.ach_clear_10_rows,
            type = AchievementType.CLEAR_ROWS,
            requiredCount = 10,
            reward = 10
        ),
        Achievement(
            id = "lines_at_once_2",
            titleId = R.string.ach_lines_at_once_2_title,
            descriptionId = R.string.ach_lines_at_once_2_desc,
            iconId = R.drawable.ach_lines_at_once_2,
            type = AchievementType.LINES_AT_ONCE,
            requiredCount = 2,
            reward = 10
        ),
        Achievement(
            id = "tiny_block",
            titleId = R.string.ach_tiny_block_title,
            descriptionId = R.string.ach_tiny_block_desc,
            iconId = R.drawable.ach_tiny_block,
            type = AchievementType.TINY_BLOCK,
            requiredCount = 1,
            reward = 10
        ),
        Achievement(
            id = "tutorial",
            titleId = R.string.ach_tutorial_title,
            descriptionId = R.string.ach_tutorial_desc,
            iconId = R.drawable.ach_tutorial,
            type = AchievementType.TUTORIAL,
            requiredCount = 1,
            reward = 10
        ),
        Achievement(
            id = "explorer",
            titleId = R.string.ach_explorer_title,
            descriptionId = R.string.ach_explorer_desc,
            iconId = R.drawable.ach_explorer,
            type = AchievementType.SECTIONS_VISITED,
            requiredCount = 3,
            reward = 10
        ),

        // Награда 15
        Achievement(
            id = "use_rotate_10",
            titleId = R.string.ach_use_rotate_10_title,
            descriptionId = R.string.ach_use_rotate_10_desc,
            iconId = BonusType.ROTATE.getIconResId(),
            type = AchievementType.USE_ROTATE,
            requiredCount = 10,
            reward = 10,
            additionalRewards = listOf(
                AchievementReward(bonusType = BonusType.ROTATE, amount = 1)
            )
        ),
        Achievement(
            id = "night_game",
            titleId = R.string.ach_night_game_title,
            descriptionId = R.string.ach_night_game_desc,
            iconId = R.drawable.ach_night_game,
            type = AchievementType.NIGHT_GAME,
            requiredCount = 1,
            reward = 10,
            additionalRewards = listOf(
                AchievementReward(bonusType = BonusType.ROTATE, amount = 1)
            )
        ),
        Achievement(
            id = "all_bonuses",
            titleId = R.string.ach_all_bonuses_title,
            descriptionId = R.string.ach_all_bonuses_desc,
            iconId = R.drawable.ach_all_bonuses,
            type = AchievementType.ALL_BONUSES_IN_GAME,
            requiredCount = 1,
            reward = 15
        ),

        // Награда 20
        Achievement(
            id = "use_freeze_10",
            titleId = R.string.ach_use_freeze_10_title,
            descriptionId = R.string.ach_use_freeze_10_desc,
            iconId = BonusType.FREEZE.getIconResId(),
            type = AchievementType.USE_FREEZE,
            requiredCount = 10,
            reward = 10,
            additionalRewards = listOf(
                AchievementReward(bonusType = BonusType.FREEZE, amount = 1)
            )
        ),
        Achievement(
            id = "use_draft_10",
            titleId = R.string.ach_use_draft_10_title,
            descriptionId = R.string.ach_use_draft_10_desc,
            iconId = BonusType.DRAFT.getIconResId(),
            type = AchievementType.USE_DRAFT,
            requiredCount = 10,
            reward = 10,
            additionalRewards = listOf(
                AchievementReward(bonusType = BonusType.DRAFT, amount = 1)
            )
        ),
        Achievement(
            id = "morning_game",
            titleId = R.string.ach_morning_game_title,
            descriptionId = R.string.ach_morning_game_desc,
            iconId = R.drawable.ach_morning_game,
            type = AchievementType.MORNING_GAME,
            requiredCount = 1,
            reward = 10,
            additionalRewards = listOf(
                AchievementReward(bonusType = BonusType.FREEZE, amount = 1)
            )
        ),
        Achievement(
            id = "roll_50_times",
            titleId = R.string.ach_roll_50_times_title,
            descriptionId = R.string.ach_roll_50_times_desc,
            iconId = R.drawable.ach_roll_50_times,
            type = AchievementType.ROLL_TIMES,
            requiredCount = 50,
            reward = 20
        ),
        Achievement(
            id = "combo_5",
            titleId = R.string.ach_combo_5_title,
            descriptionId = R.string.ach_combo_5_desc,
            iconId = R.drawable.ic_combo,
            type = AchievementType.COMBO,
            requiredCount = 5,
            reward = 20
        ),
        Achievement(
            id = "big_block",
            titleId = R.string.ach_big_block_title,
            descriptionId = R.string.ach_big_block_desc,
            iconId = R.drawable.ach_big_block,
            type = AchievementType.BIG_BLOCK,
            requiredCount = 36,
            reward = 20
        ),
        Achievement(
            id = "score_3000",
            titleId = R.string.ach_score_3000_title,
            descriptionId = R.string.ach_score_3000_desc,
            iconId = R.drawable.ach_score_3000,
            type = AchievementType.GAME_SCORE,
            requiredCount = 3000,
            reward = 20
        ),
        Achievement(
            id = "second_chance",
            titleId = R.string.ach_second_chance_title,
            descriptionId = R.string.ach_second_chance_desc,
            iconId = R.drawable.ach_second_chance,
            type = AchievementType.REVIVE_SURVIVED,
            requiredCount = 1,
            reward = 20
        ),
        Achievement(
            id = "random_bonus_10",
            titleId = R.string.ach_random_bonus_10_title,
            descriptionId = R.string.ach_random_bonus_10_desc,
            iconId = R.drawable.ach_random_bonus_10,
            type = AchievementType.RANDOM_BONUS,
            requiredCount = 10,
            reward = 20
        ),

        // Награда 25
        Achievement(
            id = "use_lucky_10",
            titleId = R.string.ach_use_lucky_10_title,
            descriptionId = R.string.ach_use_lucky_10_desc,
            iconId = BonusType.LUCKY.getIconResId(),
            type = AchievementType.USE_LUCKY,
            requiredCount = 10,
            reward = 10,
            additionalRewards = listOf(
                AchievementReward(bonusType = BonusType.LUCKY, amount = 1)
            )
        ),
        Achievement(
            id = "draft_triple",
            titleId = R.string.ach_draft_triple_title,
            descriptionId = R.string.ach_draft_triple_desc,
            iconId = R.drawable.ach_draft_triple,
            type = AchievementType.DRAFT_TRIPLE,
            requiredCount = 1,
            reward = 15,
            additionalRewards = listOf(
                AchievementReward(bonusType = BonusType.DRAFT, amount = 1)
            )
        ),
        Achievement(
            id = "double_six",
            titleId = R.string.ach_double_six_title,
            descriptionId = R.string.ach_double_six_desc,
            iconId = R.drawable.ach_double_six,
            type = AchievementType.DOUBLE_SIX_ROLLED,
            requiredCount = 3,
            reward = 20,
            additionalRewards = listOf(
                AchievementReward(bonusType = BonusType.ROTATE, amount = 1)
            )
        ),
        Achievement(
            id = "board_clear_1",
            titleId = R.string.ach_board_clear_1_title,
            descriptionId = R.string.ach_board_clear_1_desc,
            iconId = R.drawable.ach_board_clear_1,
            type = AchievementType.BOARD_CLEARED,
            requiredCount = 1,
            reward = 20,
            additionalRewards = listOf(
                AchievementReward(bonusType = BonusType.ROTATE, amount = 1)
            )
        ),
        Achievement(
            id = "first_loss",
            titleId = R.string.ach_first_loss_title,
            descriptionId = R.string.ach_first_loss_desc,
            iconId = R.drawable.ach_first_loss,
            type = AchievementType.GAMES_LOST,
            requiredCount = 1,
            reward = 20,
            additionalRewards = listOf(
                AchievementReward(bonusType = BonusType.ROTATE, amount = 1)
            )
        ),
        Achievement(
            id = "games_5",
            titleId = R.string.ach_games_5_title,
            descriptionId = R.string.ach_games_5_desc,
            iconId = R.drawable.ach_games_5,
            type = AchievementType.GAMES_PLAYED,
            requiredCount = 5,
            reward = 25
        ),

        // Награда 30
        Achievement(
            id = "clear_50_rows",
            titleId = R.string.ach_clear_50_rows_title,
            descriptionId = R.string.ach_clear_50_rows_desc,
            iconId = R.drawable.ach_clear_50_rows,
            type = AchievementType.CLEAR_ROWS,
            requiredCount = 50,
            reward = 20,
            additionalRewards = listOf(
                AchievementReward(bonusType = BonusType.FREEZE, amount = 1)
            )
        ),
        Achievement(
            id = "sniper",
            titleId = R.string.ach_sniper_title,
            descriptionId = R.string.ach_sniper_desc,
            iconId = R.drawable.ach_sniper,
            type = AchievementType.LINE_BY_TINY_BLOCK,
            requiredCount = 1,
            reward = 20,
            additionalRewards = listOf(
                AchievementReward(bonusType = BonusType.FREEZE, amount = 1)
            )
        ),
        Achievement(
            id = "roll_100_times",
            titleId = R.string.ach_roll_100_times_title,
            descriptionId = R.string.ach_roll_100_times_desc,
            iconId = R.drawable.ach_roll_100_times,
            type = AchievementType.ROLL_TIMES,
            requiredCount = 100,
            reward = 30
        ),
        Achievement(
            id = "lines_at_once_3",
            titleId = R.string.ach_lines_at_once_3_title,
            descriptionId = R.string.ach_lines_at_once_3_desc,
            iconId = R.drawable.ach_lines_at_once_3,
            type = AchievementType.LINES_AT_ONCE,
            requiredCount = 3,
            reward = 30
        ),

        // Награда 35
        Achievement(
            id = "moves_100",
            titleId = R.string.ach_moves_100_title,
            descriptionId = R.string.ach_moves_100_desc,
            iconId = R.drawable.ach_moves_100,
            type = AchievementType.MOVES_IN_GAME,
            requiredCount = 100,
            reward = 25,
            additionalRewards = listOf(
                AchievementReward(bonusType = BonusType.FREEZE, amount = 1)
            )
        ),
        Achievement(
            id = "streak_7",
            titleId = R.string.ach_streak_7_title,
            descriptionId = R.string.ach_streak_7_desc,
            iconId = R.drawable.ach_streak_7,
            type = AchievementType.DAILY_STREAK,
            requiredCount = 7,
            reward = 30,
            additionalRewards = listOf(
                AchievementReward(bonusType = BonusType.ROTATE, amount = 1)
            )
        ),

        // Награда 40
        Achievement(
            id = "clear_250_rows",
            titleId = R.string.ach_clear_250_rows_title,
            descriptionId = R.string.ach_clear_250_rows_desc,
            iconId = R.drawable.ach_clear_250_rows,
            type = AchievementType.CLEAR_ROWS,
            requiredCount = 250,
            reward = 30,
            additionalRewards = listOf(
                AchievementReward(bonusType = BonusType.DRAFT, amount = 1)
            )
        ),
        Achievement(
            id = "bank_1000",
            titleId = R.string.ach_bank_1000_title,
            descriptionId = R.string.ach_bank_1000_desc,
            iconId = R.drawable.ach_bank_1000,
            type = AchievementType.COINS_HELD,
            requiredCount = 1000,
            reward = 30,
            additionalRewards = listOf(
                AchievementReward(bonusType = BonusType.DRAFT, amount = 1)
            )
        ),
        Achievement(
            id = "spend_1000",
            titleId = R.string.ach_spend_1000_title,
            descriptionId = R.string.ach_spend_1000_desc,
            iconId = R.drawable.ach_spend_1000,
            type = AchievementType.COINS_SPENT,
            requiredCount = 1000,
            reward = 40
        ),

        // Награда 45
        Achievement(
            id = "new_year_game",
            titleId = R.string.ach_new_year_game_title,
            descriptionId = R.string.ach_new_year_game_desc,
            iconId = R.drawable.ach_new_year_game,
            type = AchievementType.NEW_YEAR_GAME,
            requiredCount = 1,
            reward = 30,
            additionalRewards = listOf(
                AchievementReward(bonusType = BonusType.LUCKY, amount = 1)
            )
        ),

        // Награда 50
        Achievement(
            id = "roll_500_times",
            titleId = R.string.ach_roll_500_times_title,
            descriptionId = R.string.ach_roll_500_times_desc,
            iconId = R.drawable.ach_roll_500_times,
            type = AchievementType.ROLL_TIMES,
            requiredCount = 500,
            reward = 40,
            additionalRewards = listOf(
                AchievementReward(bonusType = BonusType.DRAFT, amount = 1)
            )
        ),
        Achievement(
            id = "lines_at_once_4",
            titleId = R.string.ach_lines_at_once_4_title,
            descriptionId = R.string.ach_lines_at_once_4_desc,
            iconId = R.drawable.ach_lines_at_once_4,
            type = AchievementType.LINES_AT_ONCE,
            requiredCount = 4,
            reward = 40,
            additionalRewards = listOf(
                AchievementReward(bonusType = BonusType.FREEZE, amount = 1)
            )
        ),
        Achievement(
            id = "score_10000",
            titleId = R.string.ach_score_10000_title,
            descriptionId = R.string.ach_score_10000_desc,
            iconId = R.drawable.ach_score_10000,
            type = AchievementType.GAME_SCORE,
            requiredCount = 10000,
            reward = 40,
            additionalRewards = listOf(
                AchievementReward(bonusType = BonusType.DRAFT, amount = 1)
            )
        ),
        Achievement(
            id = "gifts_30",
            titleId = R.string.ach_gifts_30_title,
            descriptionId = R.string.ach_gifts_30_desc,
            iconId = R.drawable.ach_gifts_30,
            type = AchievementType.GIFTS_CLAIMED,
            requiredCount = 30,
            reward = 50
        ),

        // Награда 55
        Achievement(
            id = "combo_10",
            titleId = R.string.ach_combo_10_title,
            descriptionId = R.string.ach_combo_10_desc,
            iconId = R.drawable.ach_combo_10,
            type = AchievementType.COMBO,
            requiredCount = 10,
            reward = 40,
            additionalRewards = listOf(
                AchievementReward(bonusType = BonusType.ROTATE, amount = 3)
            )
        ),
        Achievement(
            id = "games_15",
            titleId = R.string.ach_games_15_title,
            descriptionId = R.string.ach_games_15_desc,
            iconId = R.drawable.ach_games_15,
            type = AchievementType.GAMES_PLAYED,
            requiredCount = 15,
            reward = 50,
            additionalRewards = listOf(
                AchievementReward(bonusType = BonusType.ROTATE, amount = 1)
            )
        ),

        // Награда 60
        Achievement(
            id = "board_clear_3",
            titleId = R.string.ach_board_clear_3_title,
            descriptionId = R.string.ach_board_clear_3_desc,
            iconId = R.drawable.ach_board_clear_3,
            type = AchievementType.BOARD_CLEARED_IN_GAME,
            requiredCount = 3,
            reward = 50,
            additionalRewards = listOf(
                AchievementReward(bonusType = BonusType.FREEZE, amount = 1)
            )
        ),

        // Награда 65
        Achievement(
            id = "roll_1000_times",
            titleId = R.string.ach_roll_1000_times_title,
            descriptionId = R.string.ach_roll_1000_times_desc,
            iconId = R.drawable.ach_roll_1000_times,
            type = AchievementType.ROLL_TIMES,
            requiredCount = 1000,
            reward = 50,
            additionalRewards = listOf(
                AchievementReward(bonusType = BonusType.LUCKY, amount = 1)
            )
        ),
        Achievement(
            id = "clear_1000_rows",
            titleId = R.string.ach_clear_1000_rows_title,
            descriptionId = R.string.ach_clear_1000_rows_desc,
            iconId = R.drawable.ach_clear_1000_rows,
            type = AchievementType.CLEAR_ROWS,
            requiredCount = 1000,
            reward = 50,
            additionalRewards = listOf(
                AchievementReward(bonusType = BonusType.LUCKY, amount = 1)
            )
        ),
        Achievement(
            id = "score_no_bonus",
            titleId = R.string.ach_score_no_bonus_title,
            descriptionId = R.string.ach_score_no_bonus_desc,
            iconId = R.drawable.ach_score_no_bonus,
            type = AchievementType.SCORE_NO_BONUS,
            requiredCount = 10000,
            reward = 50,
            additionalRewards = listOf(
                AchievementReward(bonusType = BonusType.LUCKY, amount = 1)
            )
        ),

        // Награда 70
        Achievement(
            id = "score_25000",
            titleId = R.string.ach_score_25000_title,
            descriptionId = R.string.ach_score_25000_desc,
            iconId = R.drawable.ach_score_25000,
            type = AchievementType.GAME_SCORE,
            requiredCount = 25000,
            reward = 60,
            additionalRewards = listOf(
                AchievementReward(bonusType = BonusType.DRAFT, amount = 1)
            )
        ),
        Achievement(
            id = "moves_200",
            titleId = R.string.ach_moves_200_title,
            descriptionId = R.string.ach_moves_200_desc,
            iconId = R.drawable.ach_moves_200,
            type = AchievementType.MOVES_IN_GAME,
            requiredCount = 200,
            reward = 60,
            additionalRewards = listOf(
                AchievementReward(bonusType = BonusType.FREEZE, amount = 1)
            )
        ),

        // Награда 90
        Achievement(
            id = "lines_at_once_6",
            titleId = R.string.ach_lines_at_once_6_title,
            descriptionId = R.string.ach_lines_at_once_6_desc,
            iconId = R.drawable.ach_lines_at_once_6,
            type = AchievementType.LINES_AT_ONCE,
            requiredCount = 6,
            reward = 75,
            additionalRewards = listOf(
                AchievementReward(bonusType = BonusType.LUCKY, amount = 1)
            )
        ),
        Achievement(
            id = "games_30",
            titleId = R.string.ach_games_30_title,
            descriptionId = R.string.ach_games_30_desc,
            iconId = R.drawable.ach_games_30,
            type = AchievementType.GAMES_PLAYED,
            requiredCount = 30,
            reward = 75,
            additionalRewards = listOf(
                AchievementReward(bonusType = BonusType.LUCKY, amount = 1)
            )
        ),

        // Награда 115
        Achievement(
            id = "score_50000",
            titleId = R.string.ach_score_50000_title,
            descriptionId = R.string.ach_score_50000_desc,
            iconId = R.drawable.ach_score_50000,
            type = AchievementType.GAME_SCORE,
            requiredCount = 50000,
            reward = 100,
            additionalRewards = listOf(
                AchievementReward(bonusType = BonusType.LUCKY, amount = 1)
            )
        ),

        // Награда 175
        Achievement(
            id = "champion",
            titleId = R.string.ach_champion_title,
            descriptionId = R.string.ach_champion_desc,
            iconId = R.drawable.ach_champion,
            type = AchievementType.ACHIEVEMENTS_CLAIMED,
            requiredCount = 49,
            reward = 150,
            additionalRewards = listOf(
                AchievementReward(bonusType = BonusType.LUCKY, amount = 1),
                AchievementReward(bonusType = BonusType.DRAFT, amount = 1)
            )
        )
    )
}

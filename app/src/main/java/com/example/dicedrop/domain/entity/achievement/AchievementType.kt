package com.example.dicedrop.domain.entity.achievement

import com.example.dicedrop.domain.entity.statistic.PlayerStat

enum class AchievementType {
    CLEAR_ROWS,
    ROLL_TIMES,
    LINES_AT_ONCE,
    BIG_BLOCK,
    GAME_SCORE,
    GAMES_PLAYED,
    BOARD_CLEARED,
    USE_ROTATE,
    USE_FREEZE,
    USE_LUCKY,
    USE_DRAFT,
    COMBO,
    DRAFT_TRIPLE,
    DOUBLE_ROLLED,
    DOUBLE_SIX_ROLLED,
    LINE_BY_TINY_BLOCK,
    TINY_BLOCK,
    BOARD_CLEARED_IN_GAME,
    SCORE_NO_BONUS,
    GAMES_LOST,
    MOVES_IN_GAME,
    REVIVE_SURVIVED,
    ALL_BONUSES_IN_GAME,
    RANDOM_BONUS,
    COINS_HELD,
    COINS_SPENT,
    DAILY_STREAK,
    GIFTS_CLAIMED,
    NIGHT_GAME,
    MORNING_GAME,
    NEW_YEAR_GAME,
    TUTORIAL,
    SECTIONS_VISITED,
    ACHIEVEMENTS_COMPLETED
}

// По какому счетчику статистики проверяется достижение
fun AchievementType.toStat(): PlayerStat = when (this) {
    AchievementType.CLEAR_ROWS -> PlayerStat.LINES_CLEARED
    AchievementType.ROLL_TIMES -> PlayerStat.ROLL_COUNT
    AchievementType.LINES_AT_ONCE -> PlayerStat.MAX_LINES_AT_ONCE
    AchievementType.BIG_BLOCK -> PlayerStat.MAX_BLOCK_AREA
    AchievementType.GAME_SCORE -> PlayerStat.BEST_GAME_SCORE
    AchievementType.GAMES_PLAYED -> PlayerStat.GAMES_PLAYED
    AchievementType.BOARD_CLEARED -> PlayerStat.BOARD_CLEARED
    AchievementType.USE_ROTATE -> PlayerStat.ROTATE_USED
    AchievementType.USE_FREEZE -> PlayerStat.FREEZE_USED
    AchievementType.USE_LUCKY -> PlayerStat.LUCKY_USED
    AchievementType.USE_DRAFT -> PlayerStat.DRAFT_USED
    AchievementType.COMBO -> PlayerStat.MAX_COMBO
    AchievementType.DRAFT_TRIPLE -> PlayerStat.DRAFT_TRIPLE
    AchievementType.DOUBLE_ROLLED -> PlayerStat.DOUBLES_ROLLED
    AchievementType.DOUBLE_SIX_ROLLED -> PlayerStat.DOUBLE_SIXES_ROLLED
    AchievementType.LINE_BY_TINY_BLOCK -> PlayerStat.LINES_BY_TINY_BLOCK
    AchievementType.TINY_BLOCK -> PlayerStat.TINY_BLOCKS_PLACED
    AchievementType.BOARD_CLEARED_IN_GAME -> PlayerStat.MAX_BOARD_CLEARS_IN_GAME
    AchievementType.SCORE_NO_BONUS -> PlayerStat.BEST_SCORE_NO_BONUS
    AchievementType.GAMES_LOST -> PlayerStat.GAMES_LOST
    AchievementType.MOVES_IN_GAME -> PlayerStat.MAX_MOVES_IN_GAME
    AchievementType.REVIVE_SURVIVED -> PlayerStat.REVIVES_SURVIVED
    AchievementType.ALL_BONUSES_IN_GAME -> PlayerStat.ALL_BONUSES_GAMES
    AchievementType.RANDOM_BONUS -> PlayerStat.RANDOM_BONUSES_RECEIVED
    AchievementType.COINS_HELD -> PlayerStat.MAX_COINS_HELD
    AchievementType.COINS_SPENT -> PlayerStat.COINS_SPENT
    AchievementType.DAILY_STREAK -> PlayerStat.MAX_DAILY_STREAK
    AchievementType.GIFTS_CLAIMED -> PlayerStat.GIFTS_CLAIMED
    AchievementType.NIGHT_GAME -> PlayerStat.NIGHT_GAME
    AchievementType.MORNING_GAME -> PlayerStat.MORNING_GAME
    AchievementType.NEW_YEAR_GAME -> PlayerStat.NEW_YEAR_GAME
    AchievementType.TUTORIAL -> PlayerStat.TUTORIAL_COMPLETED
    AchievementType.SECTIONS_VISITED -> PlayerStat.SECTIONS_OPENED
    AchievementType.ACHIEVEMENTS_COMPLETED -> PlayerStat.ACHIEVEMENTS_COMPLETED
}

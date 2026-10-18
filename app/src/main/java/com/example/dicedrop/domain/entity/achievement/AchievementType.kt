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
    COMBO
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
}

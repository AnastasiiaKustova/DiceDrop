package com.example.dicedrop.domain.entity.statistic

enum class PlayerStat {
    ROLL_COUNT,
    GAMES_PLAYED,
    LINES_CLEARED,
    COINS_EARNED,
    BOARD_CLEARED,
    ROTATE_USED,
    FREEZE_USED,
    LUCKY_USED,
    DRAFT_USED,

    // Рекорды: хранится максимальное значение, а не сумма
    MAX_LINES_AT_ONCE,
    MAX_BLOCK_AREA,
    BEST_GAME_SCORE,
    MAX_COMBO
}

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
    // Третий кубик к дублю выпал тем же числом
    DRAFT_TRIPLE,
    DOUBLES_ROLLED,
    DOUBLE_SIXES_ROLLED,
    // Блоки 1×1 и линии, собранные таким блоком
    TINY_BLOCKS_PLACED,
    LINES_BY_TINY_BLOCK,
    // Партии, закончившиеся проигрышем (отмена хода за рекламу проигрыш не отменяет)
    GAMES_LOST,
    // Продержался 3 хода после отмены хода за рекламу
    REVIVES_SURVIVED,
    // Партии, в которых использованы все четыре бонуса
    ALL_BONUSES_GAMES,
    // Случайный бонус в магазине: за монеты или за рекламу
    RANDOM_BONUSES_RECEIVED,
    COINS_SPENT,
    GIFTS_CLAIMED,

    // Рекорды: хранится максимальное значение, а не сумма
    MAX_LINES_AT_ONCE,
    MAX_BLOCK_AREA,
    BEST_GAME_SCORE,
    MAX_COMBO,
    MAX_BOARD_CLEARS_IN_GAME,
    BEST_SCORE_NO_BONUS,
    MAX_MOVES_IN_GAME,
    MAX_COINS_HELD,
    MAX_DAILY_STREAK,
    // Сколько достижений выполнено, не считая «Чемпиона»
    ACHIEVEMENTS_COMPLETED,

    // Отметки: 1 - было, 0 - не было
    NIGHT_GAME,
    MORNING_GAME,
    NEW_YEAR_GAME,
    TUTORIAL_COMPLETED,
    // Сколько разделов открыто и какие именно (битовая маска, см. AchievementTracker.Section)
    SECTIONS_OPENED,
    SECTIONS_VISITED_MASK
}

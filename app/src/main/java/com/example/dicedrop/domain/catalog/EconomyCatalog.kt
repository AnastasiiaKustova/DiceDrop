package com.example.dicedrop.domain.catalog

// Все числа экономики в одном месте: сколько монет даёт игра, подарок и рекорд.
// Цены бонусов лежат в ShopItemCatalog, награды за достижения - в AchievementsCatalog
object EconomyCatalog {

    // Старт нового игрока
    const val START_COINS = 100
    const val START_BONUS_COUNT = 3
    const val START_ROTATE_COUNT = 10

    // Монеты за игру
    const val COINS_PER_LINE = 3
    const val BOARD_CLEAR_COINS = 15

    // Ежедневный подарок по дням первой недели серии; дальше, пока серия не прервана, каждый день
    // как в последний. Каждый седьмой день серии к монетам добавляется случайный бонус
    val DAILY_COINS = listOf(75, 75, 100, 100, 125, 150, 200)
    val WEEK_DAYS = DAILY_COINS.size

    // Награда за новый рекорд растёт вместе с побитым рекордом: чем труднее было побить, тем больше.
    // Нижняя граница 20 + рекорд / 300, верхняя на четверть больше, потолок 120..150.
    // Случайный бонус - только если побит рекорд от 5 000 очков
    private const val RECORD_COINS_BASE = 20
    private const val RECORD_SCORE_PER_COIN = 300f
    private const val RECORD_COINS_SPREAD = 1.25f
    private val RECORD_COINS_MAX = 120..150
    const val RECORD_BONUS_FROM_SCORE = 5000

    // Все суммы монет кратны 5
    fun recordCoins(previousBest: Int): IntRange {
        val base = RECORD_COINS_BASE + previousBest / RECORD_SCORE_PER_COIN
        val min = roundTo5(base).coerceAtMost(RECORD_COINS_MAX.first)
        val max = roundTo5(base * RECORD_COINS_SPREAD).coerceAtMost(RECORD_COINS_MAX.last)
        return min..max
    }

    private fun roundTo5(value: Float): Int = Math.round(value / 5) * 5

    // Сколько раз в день можно получить случайный бонус в магазине за рекламу
    const val AD_BONUSES_PER_DAY = 5

    // День недели серии (1..7) по номеру дня серии (1, 2, ... - бесконечно)
    fun dayOfWeek(streak: Int): Int = (streak - 1).mod(WEEK_DAYS) + 1

    fun dailyCoins(streak: Int): Int = DAILY_COINS[(streak - 1).coerceIn(0, WEEK_DAYS - 1)]

    fun hasDailyBonus(streak: Int): Boolean = dayOfWeek(streak) == WEEK_DAYS
}

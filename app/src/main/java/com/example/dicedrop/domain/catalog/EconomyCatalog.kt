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

    // Ежедневный подарок по дням серии; на последний день к монетам добавляется случайный бонус
    val DAILY_COINS = listOf(50, 50, 75, 75, 100, 125, 150)
    val WEEK_DAYS = DAILY_COINS.size

    // Награда за новый рекорд: монеты из диапазона и один случайный бонус
    val RECORD_COINS = 50..100

    // День недели серии (1..7) по номеру дня серии (1, 2, ... - бесконечно)
    fun dayOfWeek(streak: Int): Int = (streak - 1).mod(WEEK_DAYS) + 1

    fun dailyCoins(streak: Int): Int = DAILY_COINS[dayOfWeek(streak) - 1]

    fun hasDailyBonus(streak: Int): Boolean = dayOfWeek(streak) == WEEK_DAYS
}

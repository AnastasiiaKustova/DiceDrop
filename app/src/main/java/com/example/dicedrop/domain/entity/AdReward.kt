package com.example.dicedrop.domain.entity

// За что смотрят рекламу; у каждой награды свой рекламный блок
enum class AdReward {
    // Отменить последний ход и продолжить
    REVIVE,
    // Удвоить монеты, заработанные за партию
    DOUBLE_COINS,
    // Случайный бонус в магазине, несколько раз в день
    RANDOM_BONUS
}

package com.example.dicedrop.domain.entity

// За что смотрят рекламу в конце партии
enum class AdReward {
    // Отменить последний ход и продолжить
    REVIVE,
    // Удвоить монеты, заработанные за партию
    DOUBLE_COINS
}

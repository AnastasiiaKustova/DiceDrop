package com.example.dicedrop.domain.entity.achievement

import com.example.dicedrop.domain.entity.BonusType

data class AchievementReward(
    val bonusType: BonusType,
    val amount: Int
)
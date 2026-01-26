package com.example.dicedrop.domain.entity.achievement

data class AchievementProgress(
    val id: String,
    val isRewardClaimed: Boolean = false
)

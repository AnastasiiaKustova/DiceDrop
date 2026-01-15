package com.example.dicedrop.domain.entity

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val type: AchievementType,
    val requiredCount: Int,
    val totalCount: Int = 0,
    val reward: Int,
    val isRewardClaimed: Boolean = false
)
package com.example.dicedrop.domain.entity.achievement

data class Achievement(
    val id: String,
    val titleId: Int,
    val descriptionId: Int,
    // Своя картинка у каждого достижения
    val iconId: Int,
    val type: AchievementType,
    val requiredCount: Int,
    val totalCount: Int = 0,
    val reward: Int,
    val isRewardClaimed: Boolean = false,
    val additionalRewards: List<AchievementReward> = emptyList()
)
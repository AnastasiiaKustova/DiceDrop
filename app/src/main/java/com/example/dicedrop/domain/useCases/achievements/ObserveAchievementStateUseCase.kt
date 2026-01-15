package com.example.dicedrop.domain.useCases.achievements

import com.example.dicedrop.domain.repository.AchievementRepository
import javax.inject.Inject

class ObserveAchievementStateUseCase @Inject constructor(
    private val repository: AchievementRepository
) {

    operator fun invoke() = repository.achievementState
}
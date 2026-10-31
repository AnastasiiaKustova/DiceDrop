package com.example.dicedrop.domain.useCases.achievements

import com.example.dicedrop.domain.repository.AchievementRepository
import com.example.dicedrop.domain.repository.PlayerStatsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ObserveAchievementStateUseCase @Inject constructor(
    private val repository: AchievementRepository,
    private val statsRepository: PlayerStatsRepository
) {

    // Срабатывает и на забранные награды, и на счётчики статистики: прогресс достижений зависит от обоих
    operator fun invoke(): Flow<Unit> =
        combine(repository.achievementState, statsRepository.statsFlow) { _, _ -> }
}

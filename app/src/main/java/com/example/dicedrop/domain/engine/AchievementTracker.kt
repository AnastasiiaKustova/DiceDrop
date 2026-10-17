package com.example.dicedrop.domain.engine

import com.example.dicedrop.di.ApplicationScope
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.domain.entity.achievement.toStat
import com.example.dicedrop.domain.entity.statistic.AchievementUnlockedEvent
import com.example.dicedrop.domain.entity.statistic.PlayerStat
import com.example.dicedrop.domain.repository.AchievementRepository
import com.example.dicedrop.domain.repository.PlayerStatsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

// Единая точка обновления статистики: меняет счетчик и сообщает о выполненных достижениях.
// Один экземпляр на приложение, чтобы на события подписывался один получатель (игра)
@ApplicationScope
class AchievementTracker @Inject constructor(
    private val statsRepository: PlayerStatsRepository,
    private val achievementRepository: AchievementRepository
) {

    private val _events = MutableSharedFlow<AchievementUnlockedEvent>(extraBufferCapacity = 8)
    val events: SharedFlow<AchievementUnlockedEvent> = _events

    // Накопительный счетчик: броски, линии, использованные бонусы и т.д.
    suspend fun increment(stat: PlayerStat, amount: Long = 1) {
        if (amount <= 0) return
        statsRepository.increment(stat, amount)
        val value = statsRepository.get(stat)
        notifyCrossed(stat, oldValue = value - amount, newValue = value)
    }

    // Рекорд: сохраняется, только если новое значение больше прежнего
    suspend fun updateMax(stat: PlayerStat, value: Long) {
        val oldValue = statsRepository.get(stat)
        if (value <= oldValue) return
        statsRepository.updateMax(stat, value)
        notifyCrossed(stat, oldValue = oldValue, newValue = value)
    }

    suspend fun onBonusUsed(bonusType: BonusType) {
        val stat = when (bonusType) {
            BonusType.ROTATE -> PlayerStat.ROTATE_USED
            BonusType.FREEZE -> PlayerStat.FREEZE_USED
            BonusType.LUCKY -> PlayerStat.LUCKY_USED
            BonusType.DRAFT -> PlayerStat.DRAFT_USED
            BonusType.COIN -> return
        }
        increment(stat)
    }

    // Событие отправляется только в момент пересечения порога, а не на каждом шаге после него
    private suspend fun notifyCrossed(stat: PlayerStat, oldValue: Long, newValue: Long) {
        achievementRepository.getAchievements()
            .filter { it.type.toStat() == stat && !it.isRewardClaimed }
            .filter { newValue >= it.requiredCount && oldValue < it.requiredCount }
            .forEach { achievement ->
                _events.emit(AchievementUnlockedEvent(achievement))
            }
    }
}

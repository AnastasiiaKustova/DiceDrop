package com.example.dicedrop.domain.engine

import com.example.dicedrop.di.ApplicationScope
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.domain.entity.DiceItem
import com.example.dicedrop.domain.entity.achievement.toStat
import com.example.dicedrop.domain.entity.statistic.AchievementUnlockedEvent
import com.example.dicedrop.domain.entity.statistic.PlayerStat
import com.example.dicedrop.domain.repository.AchievementRepository
import com.example.dicedrop.domain.repository.GameRepository
import com.example.dicedrop.domain.repository.PlayerStatsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

// Единая точка обновления статистики: меняет счетчик и сообщает о выполненных достижениях.
// Один экземпляр на приложение, чтобы на события подписывался один получатель (игра)
@ApplicationScope
class AchievementTracker @Inject constructor(
    private val statsRepository: PlayerStatsRepository,
    private val achievementRepository: AchievementRepository,
    gameRepository: GameRepository
) {

    private val _events = MutableSharedFlow<AchievementUnlockedEvent>(extraBufferCapacity = 8)
    val events: SharedFlow<AchievementUnlockedEvent> = _events

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Разделы для «Следопыта»: каждый - свой бит в SECTIONS_VISITED_MASK
    enum class Section(val bit: Long) { SHOP(1), ACHIEVEMENTS(2), SETTINGS(4) }

    init {
        // Монеты меняются во многих местах (линии, подарок, награды, покупки), поэтому
        // самое большое количество на руках проще отслеживать по самому кошельку
        scope.launch {
            gameRepository.coinsValueFlow.distinctUntilChanged().collect { coins ->
                updateMax(PlayerStat.MAX_COINS_HELD, coins.toLong())
            }
        }
    }

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

    // Обычный бросок двух кубиков: дубли и время, когда идёт партия
    suspend fun onDiceRolled(dice: DiceItem) {
        increment(PlayerStat.ROLL_COUNT)
        if (dice.dice1 == dice.dice2) {
            increment(PlayerStat.DOUBLES_ROLLED)
            if (dice.dice1 == 6) increment(PlayerStat.DOUBLE_SIXES_ROLLED)
        }
        val now = LocalDateTime.now()
        when (now.hour) {
            in 0..4 -> mark(PlayerStat.NIGHT_GAME)
            in 5..7 -> mark(PlayerStat.MORNING_GAME)
        }
        val isNewYear = (now.monthValue == 12 && now.dayOfMonth >= 25) ||
                (now.monthValue == 1 && now.dayOfMonth <= 10)
        if (isNewYear) mark(PlayerStat.NEW_YEAR_GAME)
    }

    suspend fun onSectionVisited(section: Section) {
        val mask = statsRepository.get(PlayerStat.SECTIONS_VISITED_MASK) or section.bit
        statsRepository.updateMax(PlayerStat.SECTIONS_VISITED_MASK, mask)
        updateMax(PlayerStat.SECTIONS_OPENED, mask.countOneBits().toLong())
    }

    // Разовая отметка: срабатывает только в первый раз
    suspend fun mark(stat: PlayerStat) = updateMax(stat, 1)

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

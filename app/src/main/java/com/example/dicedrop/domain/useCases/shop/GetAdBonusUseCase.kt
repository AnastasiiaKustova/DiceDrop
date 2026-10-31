package com.example.dicedrop.domain.useCases.shop

import com.example.dicedrop.domain.engine.AchievementTracker
import com.example.dicedrop.domain.entity.BonusItem
import com.example.dicedrop.domain.entity.statistic.PlayerStat
import com.example.dicedrop.domain.repository.ShopRepository
import javax.inject.Inject

// Случайный бонус за просмотр рекламы в магазине; null, если дневной лимит исчерпан
class GetAdBonusUseCase @Inject constructor(
    private val repository: ShopRepository,
    private val getRandomBonusUseCase: GetRandomBonusUseCase,
    private val buyBonusUseCase: BuyBonusUseCase,
    private val achievementTracker: AchievementTracker
) {
    suspend operator fun invoke(): BonusItem? {
        if (!repository.registerAdBonus()) return null
        val bonus = getRandomBonusUseCase()
        buyBonusUseCase(bonusType = bonus.type, price = 0)
        achievementTracker.increment(PlayerStat.RANDOM_BONUSES_RECEIVED)
        return bonus
    }
}

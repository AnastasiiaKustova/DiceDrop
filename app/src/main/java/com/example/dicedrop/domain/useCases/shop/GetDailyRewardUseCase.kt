package com.example.dicedrop.domain.useCases.shop

import com.example.dicedrop.domain.catalog.EconomyCatalog
import com.example.dicedrop.domain.engine.AchievementTracker
import com.example.dicedrop.domain.entity.BonusItem
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.domain.entity.getRandomBonusType
import com.example.dicedrop.domain.entity.statistic.PlayerStat
import com.example.dicedrop.domain.entity.withRewards
import com.example.dicedrop.domain.repository.GameRepository
import com.example.dicedrop.domain.repository.ShopRepository
import javax.inject.Inject

// Забирает ежедневный подарок: монеты растут по дням серии, в последний день недели - ещё и бонус.
// Возвращает полученное, чтобы показать в окне «Вы получили»
class GetDailyRewardUseCase @Inject constructor(
    private val repository: ShopRepository,
    private val gameRepository: GameRepository,
    private val achievementTracker: AchievementTracker
) {
    suspend operator fun invoke(): List<BonusItem> {
        val streak = repository.getDailyReward()

        val rewards = buildList {
            add(BonusItem(type = BonusType.COIN, count = EconomyCatalog.dailyCoins(streak)))
            if (EconomyCatalog.hasDailyBonus(streak)) {
                add(BonusItem(type = getRandomBonusType(ignoreBonusTypeList = listOf(BonusType.COIN)), count = 1))
            }
        }

        gameRepository.saveGame(gameRepository.getCurrentGame().withRewards(rewards))
        // «Неделя подряд» - по серии подарка, «Гора подарков» - по всем подаркам, не обязательно подряд
        achievementTracker.increment(PlayerStat.GIFTS_CLAIMED)
        achievementTracker.updateMax(PlayerStat.MAX_DAILY_STREAK, streak.toLong())
        return rewards
    }
}

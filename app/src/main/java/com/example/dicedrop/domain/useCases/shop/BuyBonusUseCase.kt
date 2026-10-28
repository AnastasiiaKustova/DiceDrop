package com.example.dicedrop.domain.useCases.shop

import com.example.dicedrop.core.NotEnoughCoinsException
import com.example.dicedrop.domain.engine.AchievementTracker
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.domain.entity.statistic.PlayerStat
import com.example.dicedrop.domain.repository.GameRepository
import javax.inject.Inject

class BuyBonusUseCase @Inject constructor(
    private val repository: GameRepository,
    private val achievementTracker: AchievementTracker
) {
    suspend operator fun invoke(
        bonusType: BonusType,
        price: Int = 1,
        amount: Int = 1,
    ) {
        val currentGame = repository.getCurrentGame()

        val gameStatistic = if (currentGame.coins - (price * amount) >= 0) {
            val bonuses = currentGame.bonuses.map {
                if (it.type == bonusType)
                    it.copy(count = it.count + amount)
                else it
            }
            currentGame.copy(bonuses = bonuses, coins = currentGame.coins - (price * amount))
        } else
            throw NotEnoughCoinsException("Not enough coins for buy bonus ${bonusType}")

        repository.saveGame(gameStatistic)
        // Покупки и в магазине, и в игре, когда бонус закончился; бесплатные (за рекламу) не считаются
        achievementTracker.increment(PlayerStat.COINS_SPENT, (price * amount).toLong())
    }
}

package com.example.dicedrop.domain.useCases.shop

import com.example.dicedrop.core.NotEnoughCoinsException
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.domain.entity.ShopItem
import com.example.dicedrop.domain.repository.GameRepository
import javax.inject.Inject

class BuyBonusUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(bonusType: BonusType) {
        val currentGame = repository.getCurrentGame()

        val gameStatistic = if (currentGame.coins > 0) {
            val bonuses = currentGame.bonuses.map {
                if (it.type == bonusType)
                    it.copy(count = it.count + 1)
                else it
            }
            currentGame.copy(bonuses = bonuses, coins = currentGame.coins - 1)
        } else
            throw NotEnoughCoinsException("Not enough coins for buy bonus ${bonusType}")

        repository.updateGameState(gameStatistic)

    }

    suspend operator fun invoke(shopItem: ShopItem) {
        val currentGame = repository.getCurrentGame()

        val gameStatistic = if (currentGame.coins - shopItem.price >= 0) {
            val bonuses = currentGame.bonuses.map {
                if (it.type == shopItem.bonusType)
                    it.copy(count = it.count + 1)
                else it
            }
            currentGame.copy(bonuses = bonuses, coins = currentGame.coins - shopItem.price)
        } else
            throw NotEnoughCoinsException("Not enough coins for buy bonus ${shopItem.title}")

        repository.saveGame(gameStatistic)

    }
}
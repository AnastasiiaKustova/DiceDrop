package com.example.dicedrop.domain.useCases.game

import com.example.dicedrop.domain.catalog.EconomyCatalog
import com.example.dicedrop.domain.entity.BonusItem
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.domain.entity.getRandomBonusType
import javax.inject.Inject

// Награда за новый рекорд: случайная сумма монет, кратная 5, из диапазона по побитому рекорду
// и случайный бонус, если рекорд был большим (см. EconomyCatalog)
class GetRecordRewardsUseCase @Inject constructor() {
    operator fun invoke(previousBest: Int): List<BonusItem> {
        val range = EconomyCatalog.recordCoins(previousBest)
        return buildList {
            add(
                BonusItem(
                    type = BonusType.COIN,
                    count = (range.first / 5..range.last / 5).random() * 5
                )
            )
            if (previousBest >= EconomyCatalog.RECORD_BONUS_FROM_SCORE) {
                add(
                    BonusItem(
                        type = getRandomBonusType(ignoreBonusTypeList = listOf(BonusType.COIN)),
                        count = 1
                    )
                )
            }
        }
    }
}

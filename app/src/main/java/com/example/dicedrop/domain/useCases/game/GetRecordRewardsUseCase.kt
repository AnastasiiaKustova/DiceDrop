package com.example.dicedrop.domain.useCases.game

import com.example.dicedrop.domain.catalog.EconomyCatalog
import com.example.dicedrop.domain.entity.BonusItem
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.domain.entity.getRandomBonusType
import javax.inject.Inject

// Награда за новый рекорд: монеты и один случайный бонус
class GetRecordRewardsUseCase @Inject constructor() {
    operator fun invoke(): List<BonusItem> {
        return listOf(
            BonusItem(
                type = BonusType.COIN,
                count = EconomyCatalog.RECORD_COINS.random()
            ),
            BonusItem(
                type = getRandomBonusType(ignoreBonusTypeList = listOf(BonusType.COIN)),
                count = 1
            )
        )
    }
}

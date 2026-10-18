package com.example.dicedrop.domain.useCases.shop

import com.example.dicedrop.domain.entity.BonusItem
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.domain.entity.getRandomBonusType
import javax.inject.Inject

class GetRandomBonusUseCase @Inject constructor() {
    operator fun invoke(): BonusItem {
        val bonusType = getRandomBonusType(ignoreBonusTypeList = listOf(BonusType.COIN))
        return BonusItem(type = bonusType, count = 1)
    }
}
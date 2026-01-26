package com.example.dicedrop.domain.useCases.shop

import com.example.dicedrop.domain.entity.BonusItem
import com.example.dicedrop.domain.entity.BonusType
import javax.inject.Inject
import kotlin.random.Random

class GetRandomBonusUseCase @Inject constructor() {
    operator fun invoke(): BonusItem {
        val bonuses = BonusType.entries
        val bonusID = Random.nextInt(0, bonuses.size)
        return BonusItem(type = bonuses[bonusID], count = 1)
    }
}
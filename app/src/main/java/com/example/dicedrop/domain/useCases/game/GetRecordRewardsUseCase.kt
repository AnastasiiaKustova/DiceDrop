package com.example.dicedrop.domain.useCases.game

import com.example.dicedrop.domain.entity.BonusItem
import com.example.dicedrop.domain.entity.BonusType
import javax.inject.Inject
import kotlin.random.Random

class GetRecordRewardsUseCase @Inject constructor() {
    operator fun invoke(
        newRecord: Int,
    ): List<BonusItem> {

        val coinCount =
            when(newRecord){
                in 0..1000 -> Random.nextInt(1, 4)
                in 1000..5000 -> Random.nextInt(3, 8)
                in 5000..10000 -> Random.nextInt(5, 11)
                in 10000..20000 -> Random.nextInt(5, 16)
                else -> Random.nextInt(10, 16)
            }

        val bonusCount1 =
            when(newRecord){
                in 0..1000 -> Random.nextInt(1, 2)
                in 1000..5000 -> Random.nextInt(1, 4)
                in 5000..10000 -> Random.nextInt(2, 6)
                else -> Random.nextInt(3, 6)
            }

        val bonusCount2 =
            when(newRecord){
                in 0..1000 -> Random.nextInt(1, 2)
                in 1000..5000 -> Random.nextInt(1, 4)
                in 5000..10000 -> Random.nextInt(2, 6)
                else -> Random.nextInt(3, 6)
            }



        return listOf(
            BonusItem(
                type = BonusType.COIN,
                count = coinCount
            ),
            BonusItem(
                type = randomBonusType(),
                count = bonusCount1
            ),
            BonusItem(
                type = randomBonusType(),
                count = bonusCount2
            )
        )
    }
}

fun randomBonusType(): BonusType{
    val bonuses = BonusType.entries
    val bonusID = Random.nextInt(0, bonuses.size)
    return bonuses[bonusID]
}
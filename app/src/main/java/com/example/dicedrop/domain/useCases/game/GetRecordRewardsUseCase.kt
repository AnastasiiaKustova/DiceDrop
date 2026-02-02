package com.example.dicedrop.domain.useCases.game

import com.example.dicedrop.domain.entity.BonusItem
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.presentation.game.ui.bottompanel.getRandomBonusType
import javax.inject.Inject
import kotlin.random.Random

class GetRecordRewardsUseCase @Inject constructor() {
    operator fun invoke(
        newRecord: Int,
    ): List<BonusItem> {

        val coinCount = generateRandomCoinAmount(newRecord)
        val bonusCount1 = generateRandomBonusAmount(newRecord)
        val bonusCount2 = generateRandomBonusAmount(newRecord)

        val bonusType1 = getRandomBonusType(ignoreBonusTypeList = listOf(BonusType.COIN))
        val bonusType2 = getRandomBonusType(ignoreBonusTypeList = listOf(BonusType.COIN, bonusType1))

        return listOf(
            BonusItem(
                type = BonusType.COIN,
                count = coinCount
            ),
            BonusItem(
                type = bonusType1,
                count = bonusCount1
            ),
            BonusItem(
                type = bonusType2,
                count = bonusCount2
            )
        )
    }

    companion object{
        private fun generateRandomCoinAmount(score: Int): Int{
            return when(score){
                in 0..1000 -> Random.nextInt(1, 4)
                in 1000..5000 -> Random.nextInt(3, 8)
                in 5000..10000 -> Random.nextInt(5, 11)
                in 10000..20000 -> Random.nextInt(5, 16)
                else -> Random.nextInt(10, 16)
            }
        }

        private fun generateRandomBonusAmount(score: Int): Int{
            return when(score){
                in 0..1000 -> Random.nextInt(1, 2)
                in 1000..5000 -> Random.nextInt(1, 4)
                in 5000..10000 -> Random.nextInt(2, 6)
                else -> Random.nextInt(3, 6)
            }
        }
    }
}




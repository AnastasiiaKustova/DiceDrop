package com.example.dicedrop.domain.useCases.game

import com.example.dicedrop.domain.entity.BonusItem
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.domain.repository.GameRepository
import javax.inject.Inject

class ConfirmNewRecordUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(rewardsList: List<BonusItem>) {
        val currentGameStatistic = repository.getCurrentGame()

        var coins = currentGameStatistic.coins
        var bonuses = currentGameStatistic.bonuses

        for (reward in rewardsList) {
            if (reward.type == BonusType.COIN) {
                coins += reward.count
            } else {
                bonuses = bonuses.map {
                    if (it.type == reward.type) {
                        it.copy(count = it.count + reward.count)
                    } else it
                }
            }
        }

        val newGameStatistic =  if (currentGameStatistic.totalScore > currentGameStatistic.bestScore){
            currentGameStatistic.copy(
                bestScore = currentGameStatistic.totalScore,
                coins = coins,
                bonuses = bonuses,
                isRecordAchieved = true,
                isNewRecord = false
            )
        } else{
            currentGameStatistic.copy(
                isNewRecord = false
            )
        }

        repository.updateGameState(newGameStatistic)
    }
}
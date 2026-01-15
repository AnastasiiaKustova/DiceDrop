package com.example.dicedrop.domain.useCases.game

import com.example.dicedrop.domain.repository.GameRepository
import javax.inject.Inject

class ConfirmNewRecordUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke() {
        val currentGameStatistic = repository.getCurrentGame()
        val newGameStatistic =  if (currentGameStatistic.totalScore > currentGameStatistic.bestScore){
            currentGameStatistic.copy(
                bestScore = currentGameStatistic.totalScore,
                coins = currentGameStatistic.coins + 5,
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
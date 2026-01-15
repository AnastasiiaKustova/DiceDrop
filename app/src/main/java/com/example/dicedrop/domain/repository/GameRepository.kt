package com.example.dicedrop.domain.repository

import com.example.dicedrop.domain.entity.GameStatisticItem
import kotlinx.coroutines.flow.Flow

interface GameRepository {

    val gameStatisticFlow: Flow<GameStatisticItem>

    val coinsValueFlow: Flow<Int>

    suspend fun getCurrentGame(): GameStatisticItem

    suspend fun saveGame(gameStatistic: GameStatisticItem)

    suspend fun loadGame() : GameStatisticItem

    //suspend fun updateGameState(update: (GameStatisticItem) -> GameStatisticItem)

    suspend fun updateGameState(game: GameStatisticItem)

}
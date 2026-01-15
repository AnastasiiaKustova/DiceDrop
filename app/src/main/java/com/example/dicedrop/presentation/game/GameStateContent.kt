package com.example.dicedrop.presentation.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.domain.entity.DiceItem
import com.example.dicedrop.domain.entity.DraggingType
import com.example.dicedrop.domain.entity.GameStatisticItem
import com.example.dicedrop.presentation.game.ui.board.BottomBoard
import com.example.dicedrop.presentation.game.ui.board.DrawPlayingField
import com.example.dicedrop.presentation.game.ui.bottompanel.GameBottomPanel
import com.example.dicedrop.presentation.game.ui.bottompanel.getByType
import com.example.dicedrop.presentation.game.ui.drag.BoardUiState
import com.example.dicedrop.presentation.game.ui.drag.GameUiState
import com.example.dicedrop.presentation.game.ui.drag.GameUiState.Companion.getGhost
import com.example.dicedrop.presentation.game.ui.header.GameHeader
import com.example.dicedrop.presentation.game.ui.popups.GameOverDialog
import com.example.dicedrop.presentation.game.ui.popups.NewRecordDialog
import com.example.dicedrop.presentation.ui.theme.LightBlue
import com.example.dicedrop.presentation.ui.theme.White

@Composable
fun GameStateContent(
    state: GameStore.State.GameState,
    clearedRows: List<Int>,
    clearedCols: List<Int>,
    component: GameComponent
) {
    Box(modifier = Modifier.fillMaxSize()) {

        // 1. Основной игровой экран
        when (state) {
            is GameStore.State.GameState.Loaded,
            is GameStore.State.GameState.NewRecord,
            is GameStore.State.GameState.Over -> {

                val gameState = when (state) {
                    is GameStore.State.GameState.Loaded -> state
                    is GameStore.State.GameState.NewRecord -> state.prevState
                    is GameStore.State.GameState.Over -> state.prevState
                    else -> error("Impossible")
                }

                GameScreen(
                    gameStatistic = gameState.gameStatistic,
                    clearedRows = clearedRows,
                    clearedCols = clearedCols,
                    onShopClick = component::onShopClick,
                    onRollEnd = component::onRollEnd,
                    onUseBonusClick = component::onBonusClick,
                    onBonusDragEnd = component::onPlaceBonus,
                    onBuyBonusClick = component::onBuyBonusClick,
                    onPlaceBlock = component::placeBlock
                )
            }

            GameStore.State.GameState.Loading -> Loading()
            GameStore.State.GameState.Error,
            GameStore.State.GameState.Initial -> Unit
        }

        // 2. Диалоги поверх игры
        when (state) {
            is GameStore.State.GameState.NewRecord -> {
                NewRecordDialog(
                    record = state.score,
                    confirmClick = component::onConfirmNewRecord
                )
            }

            is GameStore.State.GameState.Over -> {
                GameOverDialog(
                    gameScore = state.score,
                    onRestartClick = component::restartGame,
                    onWatchAdsClick = component::onWatchAdsClick
                )
            }

            else -> Unit
        }
    }
}

@Composable
private fun GameScreen(
    gameStatistic: GameStatisticItem,
    clearedRows: List<Int>,
    clearedCols: List<Int>,
    onShopClick: () -> Unit,
    onRollEnd: (result: DiceItem) -> Unit,
    onUseBonusClick: (BonusType) -> Unit,
    onBonusDragEnd: (r: Int, c: Int, type: BonusType) -> Unit,
    onBuyBonusClick: (BonusType) -> Unit,
    onPlaceBlock: (r: Int, c: Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val uiStates = remember { GameUiState.getList() }
        val boardState = remember { BoardUiState() }

        GameHeader(
            totalScore = gameStatistic.totalScore,
            bestScore = gameStatistic.bestScore,
            coins = gameStatistic.coins,
            onShopClick = onShopClick
        )

        DrawPlayingField(
            boardItem = gameStatistic.boardItem,
            clearedRows = clearedRows,
            clearedCols = clearedCols,
            boardState = boardState,
            diceItem = gameStatistic.dice,
            uiState = uiStates.getGhost()
        )

        BottomBoard(
            uiState = uiStates.getByType(DraggingType.BLOCK) ?: throw IllegalStateException(),
            boardItem = gameStatistic.boardItem,
            boardState = boardState,
            diceItem = gameStatistic.dice,
            onRollEnd = onRollEnd,
            onPlaceBlock = onPlaceBlock
        )

        GameBottomPanel(
            bonusStatistic = gameStatistic.bonuses,
            uiStateList = uiStates,
            onBonusClick = onUseBonusClick,
            onBonusDragEnd = onBonusDragEnd,
            boardItem = gameStatistic.boardItem,
            boardState = boardState,
            onCounterClick = { onBuyBonusClick(it.type) }
        )
    }
}

@Composable
private fun Loading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBlue)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            color = White
        )
    }
}
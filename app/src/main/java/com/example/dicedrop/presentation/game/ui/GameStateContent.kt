package com.example.dicedrop.presentation.game.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.domain.entity.DiceItem
import com.example.dicedrop.domain.entity.DraggingType
import com.example.dicedrop.domain.entity.GameStatisticItem
import com.example.dicedrop.presentation.game.GameComponent
import com.example.dicedrop.presentation.game.GameStore
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
    val configuration = LocalConfiguration.current

    val isLandscape = when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> true
        else -> false
    }

    Box(modifier = Modifier.fillMaxSize()) {

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
                    isLandscape = isLandscape,
                    onShopClick = component::onShopClick,
                    onRollEnd = component::onRollEnd,
                    onUseBonusClick = component::onBonusClick,
                    onBonusDragEnd = component::onPlaceBonus,
                    onBuyBonusClick = component::onShopClick,
                    onPlaceBlock = component::placeBlock
                )
            }

            GameStore.State.GameState.Loading -> Loading()
            GameStore.State.GameState.Error,
            GameStore.State.GameState.Initial -> Unit
        }

        when (state) {
            is GameStore.State.GameState.NewRecord -> {
                NewRecordDialog(
                    record = state.score,
                    rewardsList = state.rewardsList,
                    isLandscape = isLandscape,
                    confirmClick = { component.onConfirmNewRecord(state.rewardsList) }
                )
            }

            is GameStore.State.GameState.Over -> {
                GameOverDialog(
                    gameScore = state.score,
                    isLandscape = isLandscape,
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
    isLandscape: Boolean,
    onShopClick: () -> Unit,
    onRollEnd: (result: DiceItem) -> Unit,
    onUseBonusClick: (BonusType) -> Unit,
    onBonusDragEnd: (r: Int, c: Int, type: BonusType) -> Unit,
    onBuyBonusClick: () -> Unit,
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
            modifier = Modifier.weight(0.12f),
            totalScore = gameStatistic.totalScore,
            bestScore = gameStatistic.bestScore,
            coins = gameStatistic.coins,
            isLandscape = isLandscape,
            onShopClick = onShopClick
        )

        val content = @Composable{
            DrawPlayingField(
                modifier = Modifier.weight(0.46f),
                boardItem = gameStatistic.boardItem,
                clearedRows = clearedRows,
                clearedCols = clearedCols,
                boardState = boardState,
                diceItem = gameStatistic.dice,
                uiState = uiStates.getGhost()
            )

            BottomBoard(
                modifier = Modifier.weight(0.3f),
                uiState = uiStates.getByType(DraggingType.BLOCK)
                    ?: throw IllegalStateException(),
                boardItem = gameStatistic.boardItem,
                boardState = boardState,
                diceItem = gameStatistic.dice,
                isLandscape = isLandscape,
                onRollEnd = onRollEnd,
                onPlaceBlock = onPlaceBlock
            )

            GameBottomPanel(
                modifier = Modifier.weight(0.12f),
                bonusStatistic = gameStatistic.bonuses,
                uiStateList = uiStates,
                onBonusClick = { if (gameStatistic.dice != DiceItem.empty()) onUseBonusClick(it) },
                onBonusDragEnd = onBonusDragEnd,
                boardItem = gameStatistic.boardItem,
                boardState = boardState,
                isLandscape = isLandscape,
                onCounterClick = { onBuyBonusClick() }
            )
        }

        if (isLandscape) {
            Row(modifier = Modifier.weight(0.76f)) {
                content()
            }
        } else {
            content()
        }
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
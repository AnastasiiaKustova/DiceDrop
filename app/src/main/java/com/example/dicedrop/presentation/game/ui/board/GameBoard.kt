package com.example.dicedrop.presentation.game.ui.board

import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp
import com.example.dicedrop.domain.entity.BoardItem
import com.example.dicedrop.domain.entity.DiceItem
import com.example.dicedrop.presentation.game.ui.board.BoardUiHelper.CellState
import com.example.dicedrop.presentation.game.ui.board.BoardUiHelper.getCellColor
import com.example.dicedrop.presentation.game.ui.board.BoardUiHelper.getCellState
import com.example.dicedrop.presentation.game.ui.board.components.GameCell
import com.example.dicedrop.presentation.game.ui.board.components.Grid
import com.example.dicedrop.presentation.game.ui.drag.BoardUiState
import com.example.dicedrop.presentation.game.ui.drag.GameUiState
import com.example.dicedrop.presentation.ui.theme.LightBlue
import com.example.dicedrop.presentation.ui.theme.Yellow


@Composable
fun DrawPlayingField(
    modifier: Modifier = Modifier,
    boardItem: BoardItem,
    boardState: BoardUiState,
    clearedRows: List<Int>,
    clearedCols: List<Int>,
    diceItem: DiceItem,
    uiState: GameUiState?
) {

    Column(
        modifier = modifier
            .background(LightBlue)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .onGloballyPositioned { coords ->
                    boardState.boardOffsetX = coords.positionInRoot().x
                    boardState.boardOffsetY = coords.positionInRoot().y
                }
        ) {
            Grid(boardItem.row, boardItem.col) { r, c ->

                val isCleared = r in clearedRows || c in clearedCols

                val cellState = getCellState(uiState = uiState, boardItem = boardItem, dice = diceItem, r = r, c = c)

                val targetColor = cellState.getCellColor()

                val animatedColor by animateColorAsState(
                    targetValue = targetColor,
                    animationSpec = tween(durationMillis = 400),
                    label = "cellColor"
                )

                val infiniteTransition = rememberInfiniteTransition(label = "blink")

                val blinkColor by infiniteTransition.animateColor(
                    initialValue = Yellow,
                    targetValue = targetColor,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 400),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "blinkColor"
                )

                if (isCleared){
                    GameCell(
                        color = animatedColor,
                        size = boardState.cellSize
                    )
                }
                else if (cellState == CellState.CAN_CLEAR){
                    GameCell(
                        color = blinkColor,
                        size = boardState.cellSize
                    )
                }
                else{
                    GameCell(
                        color = targetColor,
                        size = boardState.cellSize
                    )
                }
            }
        }
    }
}




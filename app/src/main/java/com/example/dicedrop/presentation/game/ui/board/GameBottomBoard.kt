package com.example.dicedrop.presentation.game.ui.board

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.dicedrop.R
import com.example.dicedrop.domain.entity.BoardItem
import com.example.dicedrop.domain.entity.DiceItem
import com.example.dicedrop.presentation.game.ui.board.animation.AnimatedDices
import com.example.dicedrop.presentation.game.ui.board.components.GameCell
import com.example.dicedrop.presentation.game.ui.board.components.Grid
import com.example.dicedrop.presentation.game.ui.drag.BoardUiState
import com.example.dicedrop.presentation.game.ui.drag.DragController
import com.example.dicedrop.presentation.game.ui.drag.GameUiState
import com.example.dicedrop.presentation.ui.theme.DarkBlue
import com.example.dicedrop.presentation.ui.theme.LightBlue
import com.example.dicedrop.presentation.ui.theme.White
import com.example.dicedrop.presentation.ui.theme.Yellow

@Composable
fun ColumnScope.BottomBoard(
    modifier: Modifier = Modifier,
    uiState: GameUiState,
    boardItem: BoardItem,
    boardState: BoardUiState,
    diceItem: DiceItem,
    onRollEnd: (result: DiceItem) -> Unit,
    onPlaceBlock: (r: Int, c: Int) -> Unit
) {

    val rolling = remember { mutableStateOf(false) }

    if (diceItem == DiceItem.empty()) rolling.value = true

    Column(
        modifier = modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        DiceLabel(
            dice1 = diceItem.dice1,
            dice2 = diceItem.dice2,
        )

        BottomBoardField(
            dices = diceItem,
            rolling = rolling.value,
            boardItem = boardItem,
            boardState = boardState,
            uiState = uiState,
            onPlaceBlock = onPlaceBlock,
            onRollEnd = {
                rolling.value = false
                onRollEnd(it)
            }
        )
    }
}


@Composable
private fun BottomBoardField(
    modifier: Modifier = Modifier,
    uiState: GameUiState,
    boardItem: BoardItem,
    boardState: BoardUiState,
    dices: DiceItem,
    rolling: Boolean,
    onPlaceBlock: (r: Int, c: Int) -> Unit,
    onRollEnd: (result: DiceItem) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlue)
    ) {
        if (dices.dice1 > 0 && dices.dice2 > 0) {
            val density = LocalDensity.current

            Box(
                modifier = modifier
                    .offset(x = uiState.dragOffsetX, y = uiState.dragOffsetY)
                    .align(Alignment.Center)
                    .onGloballyPositioned { coords ->
                        uiState.elementStartX = coords.positionInRoot().x
                        uiState.elementStartY = coords.positionInRoot().y
                    }
                    .pointerInput(dices) {
                        detectDragGestures(
                            onDragStart = { DragController.onDragStart(uiState) },
                            onDrag = { _, dragAmount ->
                                DragController.onDrag(
                                    uiState,
                                    dragAmount,
                                    boardItem,
                                    boardState,
                                    density
                                )
                            },
                            onDragEnd = {
                                DragController.onDragEnd(uiState, boardItem, dices) { r, c, _ ->
                                    onPlaceBlock(r, c)
                                }
                            },
                            onDragCancel = { DragController.onDragCancel(uiState) }
                        )
                    }
                    .zIndex(10f)
            ) {
                Grid(dices.dice1, dices.dice2) { _, _ ->
                    GameCell(Yellow, boardState.cellSize)
                }
            }
        } else if (rolling) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
            ) {
                AnimatedDices(
                    rolling = rolling,
                    onRollEnd = {
                        onRollEnd(it)
                    }
                )
            }
        }
    }
}

@Composable
private fun DiceLabel(
    modifier: Modifier = Modifier,
    dice1: Int,
    dice2: Int
) {
    val isDiceNull = (dice1 == 0 || dice2 == 0)

    val labelText = if (!isDiceNull)
        "${dice1} x ${dice2}"
    else
        stringResource(R.string.rolling_dice)

    Box(
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .background(LightBlue)
                .height(20.dp)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(DarkBlue)
                .height(20.dp)
        )
        Box(
            modifier = modifier
                .height(40.dp)
                .width(150.dp)
                .border(width = 2.dp, color = White, shape = RoundedCornerShape(16.dp))
                .background(
                    color = DarkBlue,
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = labelText,
                fontSize = 18.sp,
                color = White
            )
        }
    }
}


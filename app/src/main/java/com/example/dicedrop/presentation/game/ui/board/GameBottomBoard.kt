package com.example.dicedrop.presentation.game.ui.board

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.dicedrop.R
import com.example.dicedrop.domain.entity.BoardItem
import com.example.dicedrop.domain.entity.DiceItem
import com.example.dicedrop.domain.entity.DiscardTarget
import com.example.dicedrop.domain.entity.TrayDie
import com.example.dicedrop.domain.entity.TrayState
import com.example.dicedrop.presentation.game.ui.board.animation.AnimatedDices
import com.example.dicedrop.presentation.game.ui.board.animation.AnimatedDie
import com.example.dicedrop.presentation.game.ui.board.components.GameCell
import com.example.dicedrop.presentation.game.ui.board.components.Grid
import com.example.dicedrop.presentation.game.ui.drag.BoardUiState
import com.example.dicedrop.presentation.game.ui.drag.DragController
import com.example.dicedrop.presentation.game.ui.drag.GameUiState
import com.example.dicedrop.presentation.ui.theme.DarkBlue
import com.example.dicedrop.presentation.ui.theme.White
import com.example.dicedrop.presentation.ui.theme.Yellow


@Composable
fun BottomBoard(
    modifier: Modifier = Modifier,
    uiState: GameUiState,
    boardItem: BoardItem,
    boardState: BoardUiState,
    diceItem: DiceItem,
    isLandscape: Boolean,
    tray: TrayState,
    facePick: Boolean,
    onDieClick: (DiscardTarget) -> Unit,
    onRollEnd: (result: DiceItem) -> Unit,
    onPlaceBlock: (r: Int, c: Int) -> Unit,
    onSpareRolled: (Int) -> Unit
) {

    val rolling = remember { mutableStateOf(false) }

    if (diceItem == DiceItem.empty()) rolling.value = true

    Column(
        modifier = modifier.fillMaxSize().background(DarkBlue).padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        DiceLabel(
            dice1 = diceItem.dice2, // левый кубик = ширина
            dice2 = diceItem.dice1, // правый кубик = высота
            frozenA = tray.slotA?.frozen == true,
            frozenB = tray.slotB?.frozen == true,
            discardMode = tray.spare?.value != null,
            thawingA = tray.slotA?.thawing == true,
            thawingB = tray.slotB?.thawing == true,
            spare = tray.spare,
            isLandscape = isLandscape,
            facePick = facePick,
            onDieClick = onDieClick,
            onSpareRolled = onSpareRolled
        )

        BottomBoardField(
            dices = diceItem,
            rolling = rolling.value,
            tray = tray,
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
    tray: TrayState,
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
                    .pointerInput(dices, boardItem) {
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
                    frozenValue = tray.slotA?.takeIf { it.frozen }?.value,
                    frozenValue2 = tray.slotB?.takeIf { it.frozen }?.value,
                    onRollEnd = { onRollEnd(DiceItem(dice1 = it.dice2, dice2 = it.dice1)) }
                )
            }
        }
    }
}

@Composable
private fun DiceLabel(
    dice1: Int,
    dice2: Int,
    frozenA: Boolean,
    frozenB: Boolean,
    discardMode: Boolean,
    spare: TrayDie?,
    thawingA: Boolean,
    thawingB: Boolean,
    isLandscape: Boolean,
    facePick: Boolean,
    onDieClick: (DiscardTarget) -> Unit,
    onSpareRolled: (Int) -> Unit
) {
    val isDiceNull = (dice1 == 0 || dice2 == 0)

    val pulse by rememberInfiniteTransition(label = "diceHint").animateFloat(
        initialValue = 1f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "diceAlpha"
    )
    val glow = if (discardMode || facePick) pulse else 1f

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (isDiceNull) {
            Text(
                text = stringResource(R.string.rolling_dice),
                fontSize = 14.sp,
                color = White
            )
        } else {
            if (discardMode) {
                Text(text = stringResource(R.string.discard_hint), fontSize = 14.sp, color = White)
                Spacer(modifier = Modifier.height(4.dp))
            } else if (facePick) {
                Text(text = stringResource(R.string.face_hint), fontSize = 14.sp, color = White)
                Spacer(modifier = Modifier.height(4.dp))
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DiceFace(dice1.toDiceRes(), frozenA, thawingA, glow) { onDieClick(DiscardTarget.A) }
                DiceFace(dice2.toDiceRes(), frozenB, thawingB, glow) { onDieClick(DiscardTarget.B) }
                if (spare != null) {
                    if (spare.value != null) {
                        DiceFace(spare.value.toDiceRes(), false, false, if (discardMode) pulse else 1f) {
                            onDieClick(DiscardTarget.SPARE)
                        }
                    } else {
                        Box(
                            modifier = Modifier.size(52.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AnimatedDie(
                                rolling = true,
                                skipAnimation = false,
                                imageSize = 28.dp,
                                boxPadding = 0.dp,
                                onRollEnd = { onSpareRolled(it) }
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun DiceFace(resId: Int?, badge: Boolean, thawing: Boolean, alpha: Float, onClick: () -> Unit) {
    val iceTarget = if (thawing) 0f else if (badge) 1f else 0f
    val ice by animateFloatAsState(iceTarget,
        animationSpec = tween(durationMillis = 400),
        label = "iceAnim"
    )

    Box(
        modifier = Modifier
            .size(52.dp)
            .alpha(alpha)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (resId != null) {
            Image(
                painter = painterResource(id = resId),
                contentDescription = null,
                modifier = Modifier
                    .size(38.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .border(2.dp, White, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "?", fontSize = 22.sp, color = White)
            }
        }
        if (ice > 0.02f) {
            Image(
                painter = painterResource(id = R.drawable.ic_ice_shell),
                contentDescription = null,
                modifier = Modifier
                    .size(54.dp)
                    .graphicsLayer {
                        scaleX = 0.9f + 0.1f * ice
                        scaleY = 0.9f + 0.1f * ice
                    }
                    .alpha(0.75f * ice)
            )
        }
    }
}

fun Int.toDiceRes(): Int = when (this) {
    1 -> R.drawable.dice_1
    2 -> R.drawable.dice_2
    3 -> R.drawable.dice_3
    4 -> R.drawable.dice_4
    5 -> R.drawable.dice_5
    else -> R.drawable.dice_6
}

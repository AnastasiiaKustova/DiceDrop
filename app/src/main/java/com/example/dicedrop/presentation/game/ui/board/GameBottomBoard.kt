package com.example.dicedrop.presentation.game.ui.board

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.dicedrop.R
import com.example.dicedrop.domain.entity.BoardItem
import com.example.dicedrop.domain.entity.DiceItem
import com.example.dicedrop.domain.entity.DiscardTarget
import com.example.dicedrop.domain.entity.TrayDie
import com.example.dicedrop.domain.entity.TrayState
import com.example.dicedrop.presentation.common.DieFace
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

private val RulerColor = White.copy(alpha = 0.6f)
private val TrayDieSize = 38.dp
private val RulerSide = 43.dp
private val RulerGap = 8.dp

@Composable
fun BottomBoard(
    modifier: Modifier = Modifier,
    uiState: GameUiState,
    boardItem: BoardItem,
    boardState: BoardUiState,
    diceItem: DiceItem,
    tray: TrayState,
    pickedFace: Int?,
    onDieClick: (DiscardTarget) -> Unit,
    onRollEnd: (result: DiceItem) -> Unit,
    onPlaceBlock: (r: Int, c: Int) -> Unit,
    onSpareRolled: (Int) -> Unit
) {

    val rolling = remember { mutableStateOf(false) }

    LaunchedEffect(diceItem) {
        if (diceItem == DiceItem.empty()) rolling.value = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBlue)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        val spare = tray.spare
        when {
            spare != null -> SpareChoice(
                slotA = tray.slotA,
                slotB = tray.slotB,
                spare = spare,
                onDieClick = onDieClick,
                onSpareRolled = onSpareRolled
            )

            diceItem.dice1 > 0 && diceItem.dice2 > 0 -> Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (pickedFace != null) {
                    LuckyHint(face = pickedFace)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                RulersWithBlock(
                    dices = diceItem,
                    tray = tray,
                    boardItem = boardItem,
                    boardState = boardState,
                    uiState = uiState,
                    facePick = pickedFace != null,
                    onDieClick = onDieClick,
                    onPlaceBlock = onPlaceBlock
                )
            }

            rolling.value -> AnimatedDices(
                rolling = rolling.value,
                frozenValue = tray.slotA?.takeIf { it.frozen }?.value,
                frozenValue2 = tray.slotB?.takeIf { it.frozen }?.value,
                onRollEnd = {
                    rolling.value = false
                    onRollEnd(DiceItem(dice1 = it.dice2, dice2 = it.dice1))
                }
            )
        }
    }
}

// Кубик ширины лежит на горизонтальной "линейке" над блоком, кубик высоты - на вертикальной слева
@Composable
private fun RulersWithBlock(
    dices: DiceItem,
    tray: TrayState,
    boardItem: BoardItem,
    boardState: BoardUiState,
    uiState: GameUiState,
    facePick: Boolean,
    onDieClick: (DiscardTarget) -> Unit,
    onPlaceBlock: (r: Int, c: Int) -> Unit
) {
    val width = dices.dice2
    val height = dices.dice1
    // Блок в лотке того же размера, что и клетки поля, чтобы при переносе он совпадал с подсветкой
    val cell = boardState.cellSize
    val blockW = cell * width
    val blockH = cell * height
    // Линейка не короче кубика с засечками, даже если блок узкий
    val rulerW = maxOf(blockW, TrayDieSize + 12.dp)
    val rulerH = maxOf(blockH, TrayDieSize + 12.dp)
    val glow = if (facePick) hintPulse() else 0f

    // Слева от блока стоит линейка высоты, поэтому сдвигаем всё на половину её ширины,
    // чтобы по центру лотка оказался сам блок, а не блок вместе с линейкой
    Column(modifier = Modifier.offset(x = -(RulerSide + RulerGap) / 2)) {
        Row {
            Spacer(modifier = Modifier.width(RulerSide + RulerGap))
            HorizontalRuler(length = rulerW) {
                TrayDieView(
                    value = width,
                    frozen = tray.slotA?.frozen == true,
                    glow = glow,
                    onClick = { onDieClick(DiscardTarget.A) }
                )
            }
        }
        Spacer(modifier = Modifier.height(RulerGap))
        Row {
            VerticalRuler(length = rulerH) {
                TrayDieView(
                    value = height,
                    frozen = tray.slotB?.frozen == true,
                    glow = glow,
                    onClick = { onDieClick(DiscardTarget.B) }
                )
            }
            Spacer(modifier = Modifier.width(RulerGap))
            Box(
                modifier = Modifier.size(rulerW, rulerH),
                contentAlignment = Alignment.Center
            ) {
                DraggableBlock(
                    dices = dices,
                    cellSize = cell,
                    boardItem = boardItem,
                    boardState = boardState,
                    uiState = uiState,
                    onPlaceBlock = onPlaceBlock
                )
            }
        }
    }
}

@Composable
private fun DraggableBlock(
    dices: DiceItem,
    cellSize: Dp,
    boardItem: BoardItem,
    boardState: BoardUiState,
    uiState: GameUiState,
    onPlaceBlock: (r: Int, c: Int) -> Unit
) {
    val density = LocalDensity.current
    Box(
        modifier = Modifier
            // Смещение читается в фазе layout, поэтому драг не вызывает рекомпозицию блока
            .offset {
                IntOffset(
                    x = uiState.dragOffsetX.roundToPx(),
                    y = uiState.dragOffsetY.roundToPx()
                )
            }
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
            GameCell(Yellow, cellSize)
        }
    }
}

@Composable
private fun HorizontalRuler(length: Dp, die: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .width(length)
            .height(RulerSide),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(modifier = Modifier.size(2.dp, 12.dp).background(RulerColor, RoundedCornerShape(1.dp)))
        Box(modifier = Modifier.weight(1f).height(2.dp).background(RulerColor, RoundedCornerShape(1.dp)))
        die()
        Box(modifier = Modifier.weight(1f).height(2.dp).background(RulerColor, RoundedCornerShape(1.dp)))
        Box(modifier = Modifier.size(2.dp, 12.dp).background(RulerColor, RoundedCornerShape(1.dp)))
    }
}

@Composable
private fun VerticalRuler(length: Dp, die: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .width(RulerSide)
            .height(length),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(modifier = Modifier.size(12.dp, 2.dp).background(RulerColor, RoundedCornerShape(1.dp)))
        Box(modifier = Modifier.weight(1f).width(2.dp).background(RulerColor, RoundedCornerShape(1.dp)))
        die()
        Box(modifier = Modifier.weight(1f).width(2.dp).background(RulerColor, RoundedCornerShape(1.dp)))
        Box(modifier = Modifier.size(12.dp, 2.dp).background(RulerColor, RoundedCornerShape(1.dp)))
    }
}

@Composable
private fun TrayDieView(
    value: Int,
    frozen: Boolean,
    glow: Float,
    onClick: () -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .size(TrayDieSize)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
    ) {
        DieFace(value = value, size = TrayDieSize, glow = glow)
        if (frozen) {
            // requiredSize: лёд больше кубика и выходит за его границы равномерно со всех сторон
            Image(
                painter = painterResource(R.drawable.ic_ice_shell),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .requiredSize(TrayDieSize + 14.dp)
                    .alpha(0.72f)
            )
            Image(
                painter = painterResource(R.drawable.freeze_badge),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 12.dp, y = (-12).dp)
                    .size(22.dp)
            )
        }
    }
}

// Подсказка "Счастливого": выбери кубик, рядом золотой кубик-иконка
@Composable
private fun LuckyHint(face: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.face_hint),
            color = White,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold
        )
        DieFace(value = face, size = 24.dp, gold = true)
    }
}

// Режим "Третий кубик": под каждым кубиком - какой блок останется, если убрать именно его
@Composable
private fun SpareChoice(
    slotA: TrayDie?,
    slotB: TrayDie?,
    spare: TrayDie,
    onDieClick: (DiscardTarget) -> Unit,
    onSpareRolled: (Int) -> Unit
) {
    val a = slotA?.value
    val b = slotB?.value
    val s = spare.value
    val ready = s != null
    val glow = if (ready) hintPulse() else 0f

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(R.string.discard_hint),
            color = White,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            // Убрали A: ширина = B, высота = третий; убрали B: ширина = A, высота = третий
            SpareOption(removed = a, blockW = b, blockH = s, glow = glow, enabled = ready) {
                onDieClick(DiscardTarget.A)
            }
            SpareOption(removed = b, blockW = a, blockH = s, glow = glow, enabled = ready) {
                onDieClick(DiscardTarget.B)
            }
            SpareOption(
                removed = s,
                blockW = a,
                blockH = b,
                glow = glow,
                enabled = ready,
                rollingSpare = !ready,
                onSpareRolled = onSpareRolled
            ) {
                onDieClick(DiscardTarget.SPARE)
            }
        }
    }
}

@Composable
private fun SpareOption(
    removed: Int?,
    blockW: Int?,
    blockH: Int?,
    glow: Float,
    enabled: Boolean,
    rollingSpare: Boolean = false,
    onSpareRolled: (Int) -> Unit = {},
    onClick: () -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    Column(
        modifier = Modifier
            .width(76.dp)
            .clickable(enabled = enabled, interactionSource = interaction, indication = null, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (rollingSpare) {
            AnimatedDie(
                rolling = true,
                dieSize = 40.dp,
                jump = 24.dp,
                onRollEnd = onSpareRolled
            )
        } else {
            DieFace(value = removed, size = 40.dp, glow = glow)
        }
        // Превью блока 6×6 занимает около 44dp
        Box(
            modifier = Modifier.height(46.dp),
            contentAlignment = Alignment.Center
        ) {
            if (blockW != null && blockH != null) {
                Column(verticalArrangement = Arrangement.spacedBy(1.5.dp)) {
                    repeat(blockH) {
                        Row(horizontalArrangement = Arrangement.spacedBy(1.5.dp)) {
                            repeat(blockW) {
                                Box(modifier = Modifier.size(6.dp).background(Yellow, RoundedCornerShape(1.5.dp)))
                            }
                        }
                    }
                }
            }
        }
        Text(
            text = if (blockW != null && blockH != null) "$blockW × $blockH" else "?",
            color = White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black
        )
    }
}

// Мягкая пульсация подсветки кубиков, когда игрок должен выбрать один из них
@Composable
private fun hintPulse(): Float {
    val pulse by rememberInfiniteTransition(label = "diceHint").animateFloat(
        initialValue = 1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "diceGlow"
    )
    return pulse
}

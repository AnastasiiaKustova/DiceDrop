package com.example.dicedrop.presentation.game.ui.board

import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
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
import com.example.dicedrop.presentation.ui.theme.Orange
import com.example.dicedrop.presentation.ui.theme.Red
import com.example.dicedrop.presentation.ui.theme.White
import com.example.dicedrop.presentation.ui.theme.Yellow
import com.example.dicedrop.presentation.ui.theme.YellowDark


@Composable
fun DrawPlayingField(
    modifier: Modifier = Modifier,
    boardItem: BoardItem,
    boardState: BoardUiState,
    clearedRows: List<Int>,
    clearedCols: List<Int>,
    comboPopup: ComboPopup?,
    diceItem: DiceItem,
    lastPlaced: List<Int> = emptyList(),
    uiState: GameUiState?
) {
    val ghostRow = uiState?.ghostRow
    val ghostCol = uiState?.ghostCol

    val clearLines = remember(boardItem, ghostRow, ghostCol, diceItem) {
        BoardUiHelper.getClearLines(boardItem, ghostRow, ghostCol, diceItem)
    }
    val isGhostValid = remember(boardItem, ghostRow, ghostCol, diceItem) {
        BoardUiHelper.canPlace(boardItem, ghostRow, ghostCol, diceItem)
    }

    // Одна анимация мигания на всё поле и только когда есть линии под очистку
    val hasClearLines = clearLines.rows.isNotEmpty() || clearLines.cols.isNotEmpty()
    val blinkColor = if (hasClearLines) blinkColor() else YellowDark

    Column(
        modifier = modifier
            .background(LightBlue)
            // Высота поля - по содержимому, остальное место достаётся лотку с кубиками
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
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

                val isNew = lastPlaced.contains(r * boardItem.col + c)

                val cellState = getCellState(
                    boardItem = boardItem,
                    dice = diceItem,
                    r = r,
                    c = c,
                    clearLines = clearLines,
                    isGhostValid = isGhostValid,
                    uiState = uiState
                )

                val targetColor = cellState.getCellColor()

                val animatedColor by animateColorAsState(
                    targetValue = targetColor,
                    animationSpec = tween(durationMillis = 400),
                    label = "cellColor"
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
                        size = boardState.cellSize,
                        isNew = isNew
                    )
                }
            }

            comboPopup?.let {
                ComboPopupText(
                    modifier = Modifier.align(Alignment.Center),
                    popup = it
                )
            }
        }
    }
}

// Надпись над полем при очистке линий; id нужен, чтобы одинаковые комбо подряд тоже анимировались
data class ComboPopup(
    val combo: Int,
    val points: Int,
    // Монеты за ход, показываются под очками
    val coins: Int = 0,
    val id: Long = System.nanoTime()
)

// Цвет комбо по уровню: до x7 оранжевый, x8+ красный
fun comboColor(combo: Int): Color = when {
    combo >= 8 -> Red
    else -> Orange
}

@Composable
private fun ComboPopupText(
    modifier: Modifier = Modifier,
    popup: ComboPopup
) {
    val progress = remember(popup.id) { Animatable(0f) }

    LaunchedEffect(popup.id) {
        progress.animateTo(1f, tween(durationMillis = 800, easing = LinearEasing))
    }

    val textStyle = TextStyle(
        fontWeight = FontWeight.Bold,
        shadow = Shadow(
            color = Color.Black.copy(alpha = 0.5f),
            offset = Offset(2f, 2f),
            blurRadius = 4f
        )
    )

    Column(
        modifier = modifier
            .graphicsLayer {
                val p = progress.value
                // Быстро вырастает с 0.6 до 1.2, оседает до 1.0, затем всплывает и тает
                val scale = when {
                    p < 0.2f -> 0.6f + p / 0.2f * 0.6f
                    p < 0.35f -> 1.2f - (p - 0.2f) / 0.15f * 0.2f
                    else -> 1f
                }
                scaleX = scale
                scaleY = scale
                translationY = -p * 40.dp.toPx()
                alpha = if (p < 0.6f) 1f else 1f - (p - 0.6f) / 0.4f
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (popup.combo >= 2) {
            Text(
                text = stringResource(R.string.combo_popup, popup.combo),
                // Голубой сливается с полем, поэтому для малых комбо - желтый
                color = if (popup.combo >= 5) comboColor(popup.combo) else Yellow,
                fontSize = 30.sp,
                style = textStyle
            )
        }
        Text(
            text = "+${popup.points}",
            color = White,
            fontSize = 26.sp,
            style = textStyle
        )
        if (popup.coins > 0) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.coin_star),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "+${popup.coins}",
                    color = Yellow,
                    fontSize = 22.sp,
                    style = textStyle
                )
            }
        }
    }
}

@Composable
private fun blinkColor(): Color {
    val blinkColor by rememberInfiniteTransition(label = "blink").animateColor(
        initialValue = Yellow,
        targetValue = YellowDark,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blinkColor"
    )
    return blinkColor
}




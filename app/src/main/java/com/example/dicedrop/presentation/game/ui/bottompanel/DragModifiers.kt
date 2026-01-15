package com.example.dicedrop.presentation.game.ui.bottompanel

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.Density
import androidx.compose.ui.zIndex
import com.example.dicedrop.domain.entity.BoardItem
import com.example.dicedrop.presentation.game.ui.drag.BoardUiState
import com.example.dicedrop.presentation.game.ui.drag.DragController
import com.example.dicedrop.presentation.game.ui.drag.GameUiState

fun DragBonusModifier(
    uiState: GameUiState,
    boardItem: BoardItem,
    boardState: BoardUiState,
    count: Int,
    density: Density,
    onBonusDragEnd: (r: Int, c: Int) -> Unit,
    modifier: Modifier = Modifier
): Modifier {

    if (count < 1) return modifier

    return modifier
        .offset(x = uiState.dragOffsetX, y = uiState.dragOffsetY)
        .onGloballyPositioned { coords ->
            uiState.elementStartX = coords.positionInRoot().x
            uiState.elementStartY = coords.positionInRoot().y
        }
        .pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    val down = awaitFirstDown()
                    DragController.onDragStartWithOffset(uiState)

                    var pointer = down
                    var isDragging = false

                    do {
                        val event = awaitPointerEvent()
                        val drag = event.changes.first()

                        if (drag.positionChange() != Offset.Zero) {
                            isDragging = true
                            DragController.onDrag(
                                gameState = uiState,
                                dragAmount = drag.positionChange(),
                                boardItem = boardItem,
                                board = boardState,
                                density = density
                            )
                            drag.consume()
                        }

                        pointer = drag
                    } while (pointer.pressed)

                    if (isDragging) {
                        DragController.onDragEnd(uiState, boardItem) { r, c, _ ->
                            onBonusDragEnd(r, c)
                        }
                    } else {
                        DragController.onDragCancel(uiState)
                    }
                }
            }
        }
        .zIndex(10f)
}
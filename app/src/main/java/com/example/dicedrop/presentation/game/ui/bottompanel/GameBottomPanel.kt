package com.example.dicedrop.presentation.game.ui.bottompanel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.dicedrop.domain.entity.BoardItem
import com.example.dicedrop.domain.entity.BonusItem
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.presentation.game.ui.drag.BoardUiState
import com.example.dicedrop.presentation.game.ui.drag.GameUiState
import com.example.dicedrop.presentation.ui.theme.BlueGradient

@Composable
fun GameBottomPanel(
    modifier: Modifier = Modifier,
    uiStateList: List<GameUiState>,
    bonusStatistic: List<BonusItem>,
    boardItem: BoardItem,
    boardState: BoardUiState,
    onCounterClick: (BonusItem) -> Unit,
    onBonusClick: () -> Unit,
    onBonusDragEnd: (r: Int, c: Int, type: BonusType) -> Unit,
) {
    val density = LocalDensity.current

    Row(
        modifier = modifier
            .background(BlueGradient)
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        bonusStatistic.forEach { bonus ->
            val uiState = uiStateList.getByType(bonus.type.toDraggingType())
            val dragModifier = if (uiState != null)
                DragBonusModifier(
                    uiState = uiState,
                    boardItem = boardItem,
                    boardState = boardState,
                    count = bonus.count,
                    density = density,
                    onBonusDragEnd = { r, c -> onBonusDragEnd(r, c, bonus.type) },
                ) else null
            BonusButton(
                iconResId = bonus.type.getIconResId(),
                count = bonus.count,
                modifier = modifier,
                dragModifier = dragModifier ?: Modifier,
                onBonusClick = onBonusClick,
                onCounterClick = { onCounterClick(bonus) }
            )
        }
    }
}





package com.example.dicedrop.presentation.game.ui.bottompanel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
    isLandscape: Boolean,
    onCounterClick: (BonusItem) -> Unit,
    onBonusClick: (BonusType) -> Unit,
    onBonusDragEnd: (r: Int, c: Int, type: BonusType) -> Unit,
) {
    val density = LocalDensity.current

    val content = @Composable{
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
                isLandscape = isLandscape,
                dragModifier = dragModifier ?: Modifier,
                onBonusClick = {onBonusClick(bonus.type)},
                onCounterClick = { if (bonus.count <= 0) onCounterClick(bonus) }
            )
        }
    }

    if (isLandscape){
        Column(
            modifier = modifier
                .background(BlueGradient)
                .fillMaxHeight()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()
        }
    }
    else{
        Row(
            modifier = modifier
                .background(BlueGradient)
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            content()
        }
    }
}
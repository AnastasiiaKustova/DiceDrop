package com.example.dicedrop.presentation.game.ui.bottompanel

import com.example.dicedrop.R
import com.example.dicedrop.domain.entity.BonusItem
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.domain.entity.DraggingType
import com.example.dicedrop.presentation.game.ui.drag.GameUiState

fun List<BonusItem>.getItemByType(bonusType: BonusType): BonusItem {
    return this.find { it.type == bonusType } ?: throw IllegalStateException()
}

fun List<GameUiState>.getByType(type: DraggingType?): GameUiState? {
    type?.let { return this.first { it.draggingType == type } } ?: return null
}

fun BonusType.toDraggingType(): DraggingType? = null // бонусы теперь по тапу, драга на доску нет

fun BonusType.getIconResId(): Int {
    return when (this) {
        BonusType.ROTATE -> R.drawable.bonus_swap_c_flat
        BonusType.FREEZE -> R.drawable.bonus_freeze_a_flat
        BonusType.LUCKY -> R.drawable.bonus_lucky_b_flat
        BonusType.DRAFT -> R.drawable.bonus_roll3_a_flat
        BonusType.COIN -> R.drawable.ic_coins
    }
}

fun getRandomBonusType(ignoreBonusTypeList: List<BonusType> = emptyList()): BonusType {
    val bonusTypeList = getBonusTypeList(ignoreBonusTypeList)
    return bonusTypeList.random()
}

fun getBonusTypeList(ignoreBonusTypeList: List<BonusType> = emptyList()): List<BonusType> {
    return BonusType.entries.filterNot { it in ignoreBonusTypeList }
}
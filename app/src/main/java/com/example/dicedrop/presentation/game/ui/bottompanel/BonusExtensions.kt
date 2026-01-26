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

fun BonusType.toDraggingType() : DraggingType?{
    return when(this){
        BonusType.BOMB -> DraggingType.BOMB
        BonusType.HAMMER -> DraggingType.HAMMER
        else -> null
    }
}

fun BonusType.getIconResId() : Int{
    return when(this){
        BonusType.BOMB -> R.drawable.ic_bomb
        BonusType.HAMMER -> R.drawable.ic_hammer
        BonusType.ROTATE -> R.drawable.ic_rotate
        BonusType.REROLL -> R.drawable.ic_update
        BonusType.COIN -> R.drawable.ic_coins
    }
}
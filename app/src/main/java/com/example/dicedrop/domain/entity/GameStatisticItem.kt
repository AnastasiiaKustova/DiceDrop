package com.example.dicedrop.domain.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GameStatisticItem (
    val bestScore: Int = 0,
    val totalScore: Int = 0,
    val coins: Int = 10000,
    val bonuses: List<BonusItem> = BonusItem.default(),
    val boardItem: BoardItem = BoardItem.default(),
    val dice: DiceItem = DiceItem.empty(),
    val nextDice: DiceItem = DiceItem.empty(),
    val tray: TrayState = TrayState(),
    val lastPlaced: List<Int> = emptyList(),
    val isRolling: Boolean = false,
    val isGameOver: Boolean = false,
    val isRecordAchieved: Boolean = false,
    val isNewRecord: Boolean = false
): Parcelable{
    companion object{
        fun default() = GameStatisticItem()
    }
}

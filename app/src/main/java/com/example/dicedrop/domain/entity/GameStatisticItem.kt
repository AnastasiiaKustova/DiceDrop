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
    val isNewRecord: Boolean = false,
    // Сколько линий очищено подряд, сбрасывается ходом без очистки
    val combo: Int = 0,
    val reviveSnapshot: ReviveSnapshot? = null
): Parcelable{
    companion object{
        fun default() = GameStatisticItem()
    }
}

// Состояние до последней установки блока, к нему откатывает просмотр рекламы после Game Over
@Parcelize
data class ReviveSnapshot(
    val boardItem: BoardItem,
    val dice: DiceItem,
    val tray: TrayState,
    val totalScore: Int,
    val combo: Int = 0
): Parcelable

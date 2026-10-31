package com.example.dicedrop.domain.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.parcelize.IgnoredOnParcel
import kotlin.random.Random

@Parcelize
data class DiceItem (
    val dice1: Int = 0,
    val dice2: Int = 0
): Parcelable{

    companion object {

        @IgnoredOnParcel
        private const val DICE_SIZE = 6

        fun empty(): DiceItem = DiceItem( dice1 = 0, dice2 = 0)

        fun randomDiceItem(): DiceItem {
            val dice1 = Random.nextInt(1, DICE_SIZE + 1)
            val dice2 = Random.nextInt(1, DICE_SIZE + 1)
            return DiceItem( dice1 = dice1, dice2 = dice2)
        }
    }
}
package com.example.dicedrop.domain.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BonusItem (
    val type: BonusType,
    val count: Int = 0
): Parcelable{
    companion object {
        fun emptyList(): List<BonusItem> =
            BonusType.entries.map { BonusItem(it) }
    }
}
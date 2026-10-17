package com.example.dicedrop.domain.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BonusItem(
    val type: BonusType,
    val count: Int = 0
) : Parcelable {
    companion object {
        fun default(): List<BonusItem> =
            BonusType.entries.filter { it in getBonusTypeList(listOf(BonusType.COIN)) }.map {
                BonusItem(it, count = when (it) {
                    else -> 99
                })
            }
    }
}
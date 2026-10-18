package com.example.dicedrop.domain.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

// Значения по умолчанию у всех полей нужны Gson для старых сохранений (см. GameSettings)
@Parcelize
data class BoardItem(
    val row: Int = 11,
    val col: Int = 11,
    val board: List<BooleanArray> =  List(row) { BooleanArray(col) }
): Parcelable {

    companion object {
        fun default(): BoardItem = BoardItem()
    }
}
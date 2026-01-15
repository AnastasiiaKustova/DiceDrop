package com.example.dicedrop.domain.entity

import android.os.Parcelable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BoardItem(
    val row: Int,
    val col: Int,
    val board: List<BooleanArray> =  List(row) { BooleanArray(col) }
): Parcelable {

    companion object {
        fun default(): BoardItem = BoardItem(
            row = 11,
            col = 11
        )
    }
}
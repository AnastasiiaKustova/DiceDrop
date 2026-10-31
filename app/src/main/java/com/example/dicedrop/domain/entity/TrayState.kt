package com.example.dicedrop.domain.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

enum class DiscardTarget { A, B, SPARE }

@Parcelize
data class TrayDie(
    val value: Int? = null,
    val frozen: Boolean = false,
) : Parcelable

enum class Slot { A, B }

enum class TrayPhase { PLACED, SHOP, ROLLED, DISCARD }

@Parcelize
data class TrayState(
    val slotA: TrayDie? = null,
    val slotB: TrayDie? = null,
    val spare: TrayDie? = null,
    val phase: TrayPhase = TrayPhase.SHOP
) : Parcelable

fun GameStatisticItem.withTray(tray: TrayState): GameStatisticItem {
    val w = tray.slotA?.value ?: 0
    val h = tray.slotB?.value ?: 0
    val newDice = DiceItem(h, w)
    // Спасающие бонусы считались для прежнего блока: без сброса окно «Блок не помещается» мелькнуло бы до новой проверки
    return copy(tray = tray, dice = newDice, rescueBonuses = if (newDice == dice) rescueBonuses else emptyList())
}
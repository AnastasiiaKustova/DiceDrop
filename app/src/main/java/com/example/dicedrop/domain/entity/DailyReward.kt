package com.example.dicedrop.domain.entity

import com.example.dicedrop.domain.catalog.EconomyCatalog
import com.example.dicedrop.domain.useCases.DateHelper

data class DailyReward(
    val lastDate: String?,
    val dayStreak: Int = 0,
    // Бонусы за рекламу в магазине: день и сколько уже получено в этот день
    val adBonusDate: String? = null,
    val adBonusCount: Int = 0
){
    // Серия жива, если подарок забран сегодня или вчера; пропущенный день обнуляет её
    val currentStreak: Int
        get() = when (lastDate) {
            DateHelper.getCurrentDate(), DateHelper.getYesterdayDate() -> dayStreak
            else -> 0
        }

    val isClaimedToday: Boolean
        get() = lastDate == DateHelper.getCurrentDate()

    // Счётчик прошлого дня не считается, поэтому в новый день снова полный лимит
    val adBonusesLeft: Int
        get() = if (adBonusDate == DateHelper.getCurrentDate()) {
            (EconomyCatalog.AD_BONUSES_PER_DAY - adBonusCount).coerceAtLeast(0)
        } else {
            EconomyCatalog.AD_BONUSES_PER_DAY
        }

    companion object{
        fun default() = DailyReward(null)
    }
}

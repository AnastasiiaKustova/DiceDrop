package com.example.dicedrop.domain.entity

import com.example.dicedrop.domain.useCases.DateHelper

data class DailyReward(
    val lastDate: String?,
    val dayStreak: Int = 0
){
    // Серия жива, если подарок забран сегодня или вчера; пропущенный день обнуляет её
    val currentStreak: Int
        get() = when (lastDate) {
            DateHelper.getCurrentDate(), DateHelper.getYesterdayDate() -> dayStreak
            else -> 0
        }

    val isClaimedToday: Boolean
        get() = lastDate == DateHelper.getCurrentDate()

    companion object{
        fun default() = DailyReward(null)
    }
}

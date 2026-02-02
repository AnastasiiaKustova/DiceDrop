package com.example.dicedrop.domain.entity

data class DailyReward(
    val lastDate: String?,
    val dayStreak: Int = 0
){
    companion object{
        fun default() = DailyReward(null)
    }
}
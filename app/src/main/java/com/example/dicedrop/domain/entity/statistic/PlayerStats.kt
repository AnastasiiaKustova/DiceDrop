package com.example.dicedrop.domain.entity.statistic

data class PlayerStats(
    val counters: Map<PlayerStat, Long> = emptyMap()
){
    companion object{
        fun PlayerStats.get(stat: PlayerStat): Long = counters[stat] ?: 0L
    }
}
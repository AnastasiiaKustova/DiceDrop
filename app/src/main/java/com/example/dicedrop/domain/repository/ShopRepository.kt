package com.example.dicedrop.domain.repository

import com.example.dicedrop.domain.entity.DailyReward
import com.example.dicedrop.domain.entity.ShopItem
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface ShopRepository {

    val rewardFlow: Flow<DailyReward>

    suspend fun isDailyRewardReceived(date: String): Boolean

    suspend fun getDailyReward()

    suspend fun getShopItems(): List<ShopItem>
}
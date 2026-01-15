package com.example.dicedrop.domain.repository

import com.example.dicedrop.domain.entity.ShopItem

interface ShopRepository {

    suspend fun isDailyRewardReceived(date: String): Boolean

    suspend fun getDailyReward()

    suspend fun getShopItems(): List<ShopItem>
}
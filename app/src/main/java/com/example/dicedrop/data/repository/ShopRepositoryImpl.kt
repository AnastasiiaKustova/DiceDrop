package com.example.dicedrop.data.repository

import com.example.dicedrop.domain.entity.ShopItem
import com.example.dicedrop.domain.repository.ShopRepository
import javax.inject.Inject

class ShopRepositoryImpl @Inject constructor() : ShopRepository {
    override suspend fun isDailyRewardReceived(date: String): Boolean {
        return true
    }

    override suspend fun getDailyReward() {

    }

    override suspend fun getShopItems(): List<ShopItem> {
        return emptyList()
    }
}
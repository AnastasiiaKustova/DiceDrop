package com.example.dicedrop.domain.useCases.shop

import com.example.dicedrop.domain.entity.ShopItem
import com.example.dicedrop.domain.repository.ShopRepository
import javax.inject.Inject

class GetShopItemsUseCase @Inject constructor(
    private val repository: ShopRepository
) {
    suspend operator fun invoke(): List<ShopItem> = repository.getShopItems()
}
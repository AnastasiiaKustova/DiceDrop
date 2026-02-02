package com.example.dicedrop.domain.entity

data class ShopItem(
    val id: String,
    val title: String,
    val iconResId: Int,
    val price: Int,
    val bonusType: BonusType?
)

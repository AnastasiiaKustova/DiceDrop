package com.example.dicedrop.domain.entity

data class ShopItem(
    val id: String,
    val titleId: Int,
    val iconResId: Int,
    val price: Int,
    val bonusType: BonusType?
)

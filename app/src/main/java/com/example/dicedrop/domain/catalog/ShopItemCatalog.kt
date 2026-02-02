package com.example.dicedrop.domain.catalog

import com.example.dicedrop.R
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.domain.entity.ShopItem
import com.example.dicedrop.presentation.game.ui.bottompanel.getIconResId


object ShopItemCatalog {
    val all: List<ShopItem> = listOf(
        ShopItem(
            id = "daily_bonus",
            title = "Daily gift",
            iconResId = R.drawable.ic_gift,
            price = 0,
            bonusType = null
        ),
        ShopItem(
            id = "basic_bonus_rotate",
            title = "Rotate",
            iconResId = BonusType.ROTATE.getIconResId(),
            price = 2,
            bonusType = BonusType.ROTATE
        ),
        ShopItem(
            id = "basic_bonus_reroll",
            title = "Reroll",
            iconResId = BonusType.REROLL.getIconResId(),
            price = 1,
            bonusType = BonusType.REROLL
        ),
        ShopItem(
            id = "basic_bonus_bomb",
            title = "Bomb",
            iconResId = BonusType.BOMB.getIconResId(),
            price = 3,
            bonusType = BonusType.BOMB
        ),
        ShopItem(
            id = "basic_bonus_hammer",
            title = "Hammer",
            iconResId = BonusType.HAMMER.getIconResId(),
            price = 3,
            bonusType = BonusType.HAMMER
        ),
        ShopItem(
            id = "random_bonus",
            title = "Random",
            iconResId = R.drawable.ic_random,
            price = 1,
            bonusType = null
        ),
    )
}
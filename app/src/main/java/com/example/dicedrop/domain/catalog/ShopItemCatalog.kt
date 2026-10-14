package com.example.dicedrop.domain.catalog

import com.example.dicedrop.R
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.domain.entity.ShopItem
import com.example.dicedrop.presentation.game.ui.bottompanel.getIconResId


object ShopItemCatalog {
    val all: List<ShopItem> = listOf(
        ShopItem(
            id = "daily_bonus",
            titleId = R.string.daily_gift,
            iconResId = R.drawable.ic_gift,
            price = 0,
            bonusType = null
        ),
        ShopItem(
            id = "basic_bonus_rotate",
            titleId = R.string.rotate,
            iconResId = BonusType.ROTATE.getIconResId(),
            price = 2,
            bonusType = BonusType.ROTATE
        ),
        ShopItem(
            id = "basic_bonus_reroll",
            titleId = R.string.reroll,
            iconResId = BonusType.REROLL.getIconResId(),
            price = 1,
            bonusType = BonusType.REROLL
        ),
        ShopItem(
            id = "basic_bonus_bomb",
            titleId = R.string.bomb,
            iconResId = BonusType.BOMB.getIconResId(),
            price = 3,
            bonusType = BonusType.BOMB
        ),
        ShopItem(
            id = "basic_bonus_hammer",
            titleId = R.string.hammer,
            iconResId = BonusType.HAMMER.getIconResId(),
            price = 3,
            bonusType = BonusType.HAMMER
        ),
        ShopItem(
            id = "random_bonus",
            titleId = R.string.random,
            iconResId = R.drawable.ic_random,
            price = 1,
            bonusType = null
        ),
    )
}
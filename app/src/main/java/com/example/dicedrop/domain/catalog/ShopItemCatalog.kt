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
            price = 1,
            bonusType = BonusType.ROTATE
        ),
        ShopItem(
            id = "basic_bonus_draft",
            titleId = R.string.draft,
            iconResId = BonusType.DRAFT.getIconResId(),
            price = 2,
            bonusType = BonusType.DRAFT
        ),
        ShopItem(
            id = "basic_bonus_freeze",
            titleId = R.string.freeze,
            iconResId = BonusType.FREEZE.getIconResId(),
            price = 2,
            bonusType = BonusType.FREEZE
        ),
        ShopItem(
            id = "basic_bonus_lucky",
            titleId = R.string.lucky,
            iconResId = BonusType.LUCKY.getIconResId(),
            price = 3,
            bonusType = BonusType.LUCKY
        ),
        ShopItem(
            id = "random_bonus",
            titleId = R.string.random,
            iconResId = R.drawable.ic_random,
            price = 2,
            bonusType = null
        ),
    )
}
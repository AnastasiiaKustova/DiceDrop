package com.example.dicedrop.domain.catalog

import com.example.dicedrop.R
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.domain.entity.ShopItem


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
            iconResId = R.drawable.bonus_swap_c_flat,
            price = 1,
            bonusType = BonusType.ROTATE
        ),
        ShopItem(
            id = "basic_bonus_draft",
            titleId = R.string.draft,
            iconResId = R.drawable.bonus_roll3_a_flat,
            price = 2,
            bonusType = BonusType.DRAFT
        ),
        ShopItem(
            id = "basic_bonus_freeze",
            titleId = R.string.freeze,
            iconResId = R.drawable.bonus_freeze_a_flat,
            price = 2,
            bonusType = BonusType.FREEZE
        ),
        ShopItem(
            id = "basic_bonus_lucky",
            titleId = R.string.lucky,
            iconResId = R.drawable.bonus_lucky_b_flat,
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
package com.example.dicedrop.domain.entity

enum class BonusType {
    ROTATE, FREEZE, LUCKY, DRAFT, COIN
}

fun getRandomBonusType(ignoreBonusTypeList: List<BonusType> = emptyList()): BonusType {
    val bonusTypeList = getBonusTypeList(ignoreBonusTypeList)
    return bonusTypeList.random()
}

fun getBonusTypeList(ignoreBonusTypeList: List<BonusType> = emptyList()): List<BonusType> {
    return BonusType.entries.filterNot { it in ignoreBonusTypeList }
}

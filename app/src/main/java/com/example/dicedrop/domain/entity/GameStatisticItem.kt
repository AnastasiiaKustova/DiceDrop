package com.example.dicedrop.domain.entity

import android.os.Parcelable
import com.example.dicedrop.domain.catalog.EconomyCatalog
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GameStatisticItem (
    val bestScore: Int = 0,
    val totalScore: Int = 0,
    val coins: Int = EconomyCatalog.START_COINS,
    val bonuses: List<BonusItem> = BonusItem.default(),
    val boardItem: BoardItem = BoardItem.default(),
    val dice: DiceItem = DiceItem.empty(),
    val nextDice: DiceItem = DiceItem.empty(),
    val tray: TrayState = TrayState(),
    val lastPlaced: List<Int> = emptyList(),
    val isRolling: Boolean = false,
    val isGameOver: Boolean = false,
    val isRecordAchieved: Boolean = false,
    val isNewRecord: Boolean = false,
    // Сколько линий очищено подряд, сбрасывается ходом без очистки
    val combo: Int = 0,
    val reviveSnapshot: ReviveSnapshot? = null,
    // Монеты, заработанные в этой партии; показываются в конце игры и удваиваются за рекламу
    val coinsEarned: Int = 0,
    val coinsDoubled: Boolean = false,
    // Счётчики партии для достижений: ходы, полные очистки поля, какие бонусы использованы
    val moves: Int = 0,
    val boardClears: Int = 0,
    val usedBonuses: List<BonusType> = emptyList(),
    // Ходы после отмены хода за рекламу; null - отмены не было или «Второй шанс» уже засчитан
    val movesAfterRevive: Int? = null,
    // Блок никуда не встаёт, но его спасают эти бонусы; пусто - ход есть или игра окончена
    val rescueBonuses: List<BonusType> = emptyList()
): Parcelable{
    companion object{
        fun default() = GameStatisticItem()
    }
}

// Начислить награды: монеты в кошелёк, бонусы в запас
fun GameStatisticItem.withRewards(rewards: List<BonusItem>): GameStatisticItem {
    var coins = coins
    var bonuses = bonuses
    for (reward in rewards) {
        if (reward.type == BonusType.COIN) {
            coins += reward.count
        } else {
            bonuses = bonuses.map {
                if (it.type == reward.type) it.copy(count = it.count + reward.count) else it
            }
        }
    }
    return copy(coins = coins, bonuses = bonuses)
}

// Состояние до последней установки блока, к нему откатывает просмотр рекламы после Game Over
@Parcelize
data class ReviveSnapshot(
    val boardItem: BoardItem,
    val dice: DiceItem,
    val tray: TrayState,
    val totalScore: Int,
    val combo: Int = 0
): Parcelable

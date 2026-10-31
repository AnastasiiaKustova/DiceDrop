package com.example.dicedrop

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.dicedrop.data.repository.AchievementRepositoryImpl
import com.example.dicedrop.data.repository.GameRepositoryImpl
import com.example.dicedrop.data.repository.PlayerStatsRepositoryImpl
import com.example.dicedrop.data.repository.ShopRepositoryImpl
import com.example.dicedrop.domain.action.CheckAndClearFullRowsAction
import com.example.dicedrop.domain.engine.AchievementTracker
import com.example.dicedrop.domain.entity.BoardItem
import com.example.dicedrop.domain.entity.BonusItem
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.domain.entity.DiceItem
import com.example.dicedrop.domain.entity.GameStatisticItem
import com.example.dicedrop.domain.entity.TrayDie
import com.example.dicedrop.domain.entity.TrayState
import com.example.dicedrop.domain.useCases.achievements.GetAchievementRewardUseCase
import com.example.dicedrop.domain.useCases.achievements.GetAchievementsUseCase
import com.example.dicedrop.domain.useCases.game.CheckAndClearFullRowsUseCase
import com.example.dicedrop.domain.useCases.game.CheckGameOverUseCase
import com.example.dicedrop.domain.useCases.game.ConfirmNewRecordUseCase
import com.example.dicedrop.domain.useCases.game.DiscardSpareUseCase
import com.example.dicedrop.domain.useCases.game.DoubleCoinsUseCase
import com.example.dicedrop.domain.useCases.game.GrantAdRewardUseCase
import com.example.dicedrop.domain.useCases.game.PlaceBlockUseCase
import com.example.dicedrop.domain.useCases.game.RestartGameUseCase
import com.example.dicedrop.domain.useCases.game.RollDicesUseCase
import com.example.dicedrop.domain.useCases.game.RollSpareUseCase
import com.example.dicedrop.domain.useCases.game.UseBonusUseCase
import com.example.dicedrop.domain.useCases.shop.BuyBonusUseCase
import com.example.dicedrop.domain.useCases.shop.GetAdBonusUseCase
import com.example.dicedrop.domain.useCases.shop.GetDailyRewardUseCase
import com.example.dicedrop.domain.useCases.shop.GetRandomBonusUseCase
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

// Хранилище в памяти вместо файла на телефоне
class MemoryDataStore(initial: Map<String, String> = emptyMap()) : DataStore<Preferences> {
    private val mutex = Mutex()
    private val state = MutableStateFlow<Preferences>(
        mutablePreferencesOf().apply {
            initial.forEach { (key, value) -> this[stringPreferencesKey(key)] = value }
        }
    )
    override val data = state

    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences =
        mutex.withLock { transform(state.value).also { state.value = it } }
}

// Настоящие репозитории и сценарии игры поверх хранилища в памяти: всё как в игре, только без телефона
class TestWorld(val store: MemoryDataStore = MemoryDataStore()) {
    private val gson = Gson()
    val games = GameRepositoryImpl(store, gson)
    val stats = PlayerStatsRepositoryImpl(store, gson)
    val achievements = AchievementRepositoryImpl(store, gson)
    val shop = ShopRepositoryImpl(store, gson)
    val tracker = AchievementTracker(stats, achievements, games)

    val place = PlaceBlockUseCase(games, tracker)
    val clear = CheckAndClearFullRowsAction(CheckAndClearFullRowsUseCase(games), tracker)
    val checkGameOver = CheckGameOverUseCase(games, tracker)
    val confirmRecord = ConfirmNewRecordUseCase(games)
    val revive = GrantAdRewardUseCase(games)
    val doubleCoins = DoubleCoinsUseCase(games)
    val restart = RestartGameUseCase(games, tracker)
    val roll = RollDicesUseCase(games)
    val rollSpare = RollSpareUseCase(games, tracker)
    val discard = DiscardSpareUseCase(games)
    val useBonus = UseBonusUseCase(games, tracker)
    val buy = BuyBonusUseCase(games, tracker)
    val adBonus = GetAdBonusUseCase(shop, GetRandomBonusUseCase(), buy, tracker)
    val daily = GetDailyRewardUseCase(shop, games, tracker)
    val getAchievements = GetAchievementsUseCase(achievements, stats)
    val claim = GetAchievementRewardUseCase(achievements, games, getAchievements)

    suspend fun game(): GameStatisticItem = games.getCurrentGame()

    suspend fun setGame(change: GameStatisticItem.() -> GameStatisticItem) {
        games.saveGame(game().change())
    }

    // Партия с заданным блоком в лотке: h - высота, w - ширина
    suspend fun setDice(h: Int, w: Int) = setGame { withDice(h, w) }

    // Ставит блок и сразу очищает линии, как игра после установки
    suspend fun placeAndClear(row: Int, col: Int) = clear(place(row, col)!!)
}

fun GameStatisticItem.withDice(h: Int, w: Int) = copy(
    dice = DiceItem(h, w),
    tray = TrayState(slotA = TrayDie(w), slotB = TrayDie(h))
)

fun GameStatisticItem.withBonuses(rotate: Int = 0, freeze: Int = 0, lucky: Int = 0, draft: Int = 0) = copy(
    bonuses = listOf(
        BonusItem(BonusType.ROTATE, rotate),
        BonusItem(BonusType.FREEZE, freeze),
        BonusItem(BonusType.LUCKY, lucky),
        BonusItem(BonusType.DRAFT, draft)
    )
)

fun GameStatisticItem.bonusCount(type: BonusType) = bonuses.first { it.type == type }.count

// Поле: занято всё, кроме перечисленных клеток
fun fullBoardExcept(vararg free: Pair<Int, Int>): BoardItem {
    val empty = BoardItem.default()
    return empty.copy(board = List(empty.row) { r -> BooleanArray(empty.col) { c -> (r to c) !in free } })
}

// Поле: занята полностью строка row, кроме столбца freeCol
fun rowAlmostFull(row: Int, freeCol: Int, base: BoardItem = BoardItem.default()) = base.copy(
    board = base.board.mapIndexed { r, line ->
        if (r == row) BooleanArray(line.size) { c -> c != freeCol } else line.copyOf()
    }
)

suspend fun TestWorld.stat(stat: com.example.dicedrop.domain.entity.statistic.PlayerStat) = stats.get(stat)

suspend fun TestWorld.currentStats() = stats.statsFlow.first()

fun noData(): Preferences = emptyPreferences()

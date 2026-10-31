package com.example.dicedrop

import com.example.dicedrop.domain.catalog.AchievementsCatalog
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.domain.entity.achievement.AchievementType
import com.example.dicedrop.domain.entity.achievement.toStat
import com.example.dicedrop.domain.entity.statistic.AchievementUnlockedEvent
import com.example.dicedrop.domain.entity.statistic.PlayerStat
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AchievementsTest {
    private val all = AchievementsCatalog.all

    @Test
    fun `каталог без дублей и ошибок в наградах`() {
        assertEquals("id", all.size, all.map { it.id }.toSet().size)
        assertEquals("названия", all.size, all.map { it.titleId }.toSet().size)
        assertEquals("картинки", all.size, all.map { it.iconId }.toSet().size)
        all.forEach { a ->
            assertTrue("${a.id}: требование", a.requiredCount > 0)
            assertTrue("${a.id}: монеты", a.reward > 0)
            assertEquals("${a.id}: кратность 5", 0, a.reward % 5)
            a.additionalRewards.forEach {
                assertTrue("${a.id}: количество бонуса", it.amount > 0)
                assertTrue("${a.id}: бонус не монета", it.bonusType != BonusType.COIN)
            }
        }
    }

    @Test
    fun `все условия достижимы в игре`() {
        val limits = mapOf(
            AchievementType.BIG_BLOCK to 36,          // Самый большой блок 6 × 6
            AchievementType.LINES_AT_ONCE to 12,      // Блок 6 × 6 закрывает не больше 6 строк и 6 столбцов
            AchievementType.SECTIONS_VISITED to 3,    // Магазин, достижения, настройки
            AchievementType.TUTORIAL to 1,
            AchievementType.NIGHT_GAME to 1,
            AchievementType.MORNING_GAME to 1,
            AchievementType.NEW_YEAR_GAME to 1
        )
        all.forEach { a ->
            limits[a.type]?.let { limit ->
                assertTrue("${a.id}: нужно ${a.requiredCount}, максимум $limit", a.requiredCount <= limit)
            }
        }
    }

    @Test
    fun `чемпион требует все остальные достижения`() {
        val champion = all.single { it.type == AchievementType.ACHIEVEMENTS_COMPLETED }
        assertEquals(all.size - 1, champion.requiredCount)
    }

    @Test
    fun `порядок в каталоге идёт по возрастанию награды`() {
        val prices = mapOf(
            BonusType.ROTATE to 5, BonusType.FREEZE to 10, BonusType.DRAFT to 10, BonusType.LUCKY to 15
        )
        val values = all.map { a -> a.reward + a.additionalRewards.sumOf { prices.getValue(it.bonusType) * it.amount } }
        assertEquals(values.sorted(), values)
    }

    @Test
    fun `награду нельзя забрать раньше времени и дважды`() = runBlocking {
        val w = TestWorld()
        val a = all.first { it.id == "double_roll" }
        val coins = w.game().coins
        assertFalse(w.claim(a.id))
        assertEquals(coins, w.game().coins)

        w.tracker.increment(a.type.toStat())
        assertTrue(w.claim(a.id))
        assertEquals(coins + a.reward, w.game().coins)
        assertFalse(w.claim(a.id))
        assertEquals(coins + a.reward, w.game().coins)
    }

    @Test
    fun `двойное нажатие одновременно не выдаёт награду дважды`() = runBlocking {
        val w = TestWorld()
        val a = all.first { it.id == "double_roll" }
        w.tracker.increment(a.type.toStat())
        val coins = w.game().coins
        val results = List(8) { async(Dispatchers.Default) { w.claim(a.id) } }.awaitAll()
        assertEquals(1, results.count { it })
        assertEquals(coins + a.reward, w.game().coins)
    }

    @Test
    fun `бонусы из награды попадают в запас`() = runBlocking {
        val w = TestWorld()
        val a = all.first { it.additionalRewards.isNotEmpty() && it.type != AchievementType.ACHIEVEMENTS_COMPLETED }
        w.tracker.updateMax(a.type.toStat(), a.requiredCount.toLong())
        val before = w.game().bonusCount(a.additionalRewards[0].bonusType)
        w.claim(a.id)
        assertEquals(before + a.additionalRewards[0].amount, w.game().bonusCount(a.additionalRewards[0].bonusType))
    }

    @Test
    fun `событие о выполнении приходит один раз при пересечении порога`() = runBlocking {
        val w = TestWorld()
        val events = mutableListOf<AchievementUnlockedEvent>()
        val job = launch(Dispatchers.Unconfined, start = CoroutineStart.UNDISPATCHED) {
            w.tracker.events.collect { events += it }
        }
        val a = all.first { it.id == "clear_10_rows" }
        w.tracker.increment(PlayerStat.LINES_CLEARED, 9)
        assertTrue(events.none { it.achievement.id == a.id })
        w.tracker.increment(PlayerStat.LINES_CLEARED)
        w.tracker.increment(PlayerStat.LINES_CLEARED)
        delay(50)
        assertEquals(1, events.count { it.achievement.id == a.id })
        job.cancel()
    }

    @Test
    fun `рекордный счётчик не уменьшается`() = runBlocking {
        val w = TestWorld()
        w.tracker.updateMax(PlayerStat.BEST_GAME_SCORE, 500)
        w.tracker.updateMax(PlayerStat.BEST_GAME_SCORE, 100)
        assertEquals(500L, w.stat(PlayerStat.BEST_GAME_SCORE))
    }

    @Test
    fun `чемпион считает выполненные, даже если награды не забраны`() = runBlocking {
        val w = TestWorld()
        val others = all.filter { it.type != AchievementType.ACHIEVEMENTS_COMPLETED }
        others.groupBy { it.type.toStat() }.forEach { (stat, list) ->
            w.tracker.updateMax(stat, list.maxOf { it.requiredCount }.toLong())
        }
        val champion = w.getAchievements().single { it.type == AchievementType.ACHIEVEMENTS_COMPLETED }
        assertEquals(others.size, champion.totalCount)
        assertTrue(w.getAchievements().none { it.isRewardClaimed })
    }

    @Test
    fun `чемпион выполняется на последнем достижении и выдаёт большую награду`() = runBlocking {
        val w = TestWorld()
        val others = all.filter { it.type != AchievementType.ACHIEVEMENTS_COMPLETED }
        val stats = others.groupBy { it.type.toStat() }
        stats.entries.drop(1).forEach { (stat, list) ->
            w.tracker.updateMax(stat, list.maxOf { it.requiredCount }.toLong())
        }
        assertFalse(w.claim("champion"))

        val (lastStat, list) = stats.entries.first()
        w.tracker.updateMax(lastStat, list.maxOf { it.requiredCount }.toLong())
        val coins = w.game().coins
        val lucky = w.game().bonusCount(BonusType.LUCKY)
        assertTrue(w.claim("champion"))
        assertEquals(coins + 500, w.game().coins)
        assertEquals(lucky + 5, w.game().bonusCount(BonusType.LUCKY))
    }

    @Test
    fun `забранные награды переживают перезапуск`() = runBlocking {
        val store = MemoryDataStore()
        val w = TestWorld(store)
        w.tracker.increment(PlayerStat.DOUBLES_ROLLED)
        assertTrue(w.claim("double_roll"))

        val reopened = TestWorld(store)
        assertTrue(reopened.getAchievements().first { it.id == "double_roll" }.isRewardClaimed)
        assertFalse(reopened.claim("double_roll"))
    }

    @Test
    fun `следопыт считает разные разделы, а не заходы`() = runBlocking {
        val w = TestWorld()
        val section = com.example.dicedrop.domain.engine.AchievementTracker.Section.SHOP
        repeat(3) { w.tracker.onSectionVisited(section) }
        assertEquals(1L, w.stat(PlayerStat.SECTIONS_OPENED))
        w.tracker.onSectionVisited(com.example.dicedrop.domain.engine.AchievementTracker.Section.SETTINGS)
        w.tracker.onSectionVisited(com.example.dicedrop.domain.engine.AchievementTracker.Section.ACHIEVEMENTS)
        assertEquals(3L, w.stat(PlayerStat.SECTIONS_OPENED))
    }

    @Test
    fun `счётчики не теряются при одновременных обновлениях`() = runBlocking {
        val w = TestWorld()
        val stats = listOf(PlayerStat.ROLL_COUNT, PlayerStat.LINES_CLEARED, PlayerStat.COINS_EARNED, PlayerStat.GAMES_PLAYED)
        stats.map { stat ->
            launch(Dispatchers.Default) { repeat(500) { w.stats.increment(stat) } }
        }.forEach { it.join() }
        stats.forEach { assertEquals("$it", 500L, w.stat(it)) }
    }
}

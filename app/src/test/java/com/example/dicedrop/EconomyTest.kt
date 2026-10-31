package com.example.dicedrop

import com.example.dicedrop.core.NotEnoughCoinsException
import com.example.dicedrop.domain.catalog.EconomyCatalog
import com.example.dicedrop.domain.catalog.ShopItemCatalog
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.domain.entity.DailyReward
import com.example.dicedrop.domain.entity.statistic.PlayerStat
import com.example.dicedrop.domain.useCases.DateHelper
import com.example.dicedrop.domain.useCases.game.GetRecordRewardsUseCase
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

// Подарок, магазин, реклама, награды за рекорд и сохранения
class EconomyTest {

    private fun worldWithGift(lastDate: String?, streak: Int) = TestWorld(
        MemoryDataStore(mapOf("player_daily_reward_json" to Gson().toJson(DailyReward(lastDate, streak))))
    )

    private val yesterday get() = DateHelper.getYesterdayDate()
    private val today get() = DateHelper.getCurrentDate()

    @Test
    fun `первый подарок - первый день`() = runBlocking {
        val w = TestWorld()
        val coins = w.game().coins
        val rewards = w.daily()
        assertEquals(1, rewards.size)
        assertEquals(EconomyCatalog.DAILY_COINS[0], rewards[0].count)
        assertEquals(coins + EconomyCatalog.DAILY_COINS[0], w.game().coins)
        assertEquals(1, w.shop.rewardFlow.first().dayStreak)
        assertTrue(w.shop.isDailyRewardReceived(today))
    }

    @Test
    fun `серия продолжается, если вчера забирали`() = runBlocking {
        val w = worldWithGift(yesterday, 2)
        assertEquals(EconomyCatalog.DAILY_COINS[2], w.daily()[0].count)
        assertEquals(3, w.shop.rewardFlow.first().dayStreak)
    }

    @Test
    fun `пропущенный день начинает серию заново`() = runBlocking {
        val w = worldWithGift("2020-01-01", 6)
        assertEquals(0, w.shop.rewardFlow.first().currentStreak)
        assertEquals(EconomyCatalog.DAILY_COINS[0], w.daily()[0].count)
        assertEquals(1, w.shop.rewardFlow.first().dayStreak)
    }

    @Test
    fun `бонус только на каждый седьмой день, монеты после недели не падают`() = runBlocking {
        val last = EconomyCatalog.DAILY_COINS.last()
        for (day in 1..30) {
            val w = worldWithGift(yesterday, day - 1)
            val rewards = w.daily()
            val expected = EconomyCatalog.DAILY_COINS.getOrElse(day - 1) { last }
            assertEquals("монеты в день $day", expected, rewards[0].count)
            assertEquals("бонус в день $day", day % 7 == 0, rewards.size == 2)
            if (rewards.size == 2) {
                assertTrue(rewards[1].type != BonusType.COIN)
                assertEquals(1, rewards[1].count)
            }
        }
    }

    @Test
    fun `бонус подарка действительно попадает в запас`() = runBlocking {
        val w = worldWithGift(yesterday, 6)
        val before = w.game()
        val rewards = w.daily()
        val after = w.game()
        assertEquals(before.bonusCount(rewards[1].type) + 1, after.bonusCount(rewards[1].type))
        assertEquals(before.coins + rewards[0].count, after.coins)
    }

    @Test
    fun `подарок считается в достижениях`() = runBlocking {
        val w = worldWithGift(yesterday, 6)
        w.daily()
        assertEquals(1L, w.stat(PlayerStat.GIFTS_CLAIMED))
        assertEquals(7L, w.stat(PlayerStat.MAX_DAILY_STREAK))
    }

    @Test
    fun `покупка списывает монеты и добавляет бонус`() = runBlocking {
        val w = TestWorld()
        w.setGame { copy(coins = 10).withBonuses() }
        w.buy(BonusType.LUCKY, price = 10)
        assertEquals(0, w.game().coins)
        assertEquals(1, w.game().bonusCount(BonusType.LUCKY))
        assertEquals(10L, w.stat(PlayerStat.COINS_SPENT))
    }

    @Test
    fun `без монет купить нельзя и ничего не меняется`() = runBlocking {
        val w = TestWorld()
        w.setGame { copy(coins = 4).withBonuses() }
        assertThrows(NotEnoughCoinsException::class.java) {
            runBlocking { w.buy(BonusType.ROTATE, price = 5) }
        }
        assertEquals(4, w.game().coins)
        assertEquals(0, w.game().bonusCount(BonusType.ROTATE))
        assertEquals(0L, w.stat(PlayerStat.COINS_SPENT))
    }

    @Test
    fun `бонусы за рекламу бесплатны и ограничены в день`() = runBlocking {
        val w = TestWorld()
        w.setGame { copy(coins = 0).withBonuses() }
        repeat(EconomyCatalog.AD_BONUSES_PER_DAY) {
            assertTrue(w.adBonus() != null)
        }
        assertNull("лимит исчерпан", w.adBonus())
        val total = w.game().bonuses.sumOf { it.count }
        assertEquals(EconomyCatalog.AD_BONUSES_PER_DAY, total)
        assertEquals(0, w.game().coins)
        assertEquals(0L, w.stat(PlayerStat.COINS_SPENT))
        assertEquals(EconomyCatalog.AD_BONUSES_PER_DAY.toLong(), w.stat(PlayerStat.RANDOM_BONUSES_RECEIVED))
    }

    @Test
    fun `в новый день лимит рекламы снова полный`() = runBlocking {
        val reward = DailyReward(null, adBonusDate = yesterday, adBonusCount = 5)
        assertEquals(EconomyCatalog.AD_BONUSES_PER_DAY, reward.adBonusesLeft)
        val used = DailyReward(null, adBonusDate = today, adBonusCount = 99)
        assertEquals(0, used.adBonusesLeft)
    }

    @Test
    fun `награда за рекорд кратна 5 и в пределах диапазона при любом рекорде`() {
        val rewards = GetRecordRewardsUseCase()
        for (best in listOf(0, 1, 100, 299, 300, 1000, 4999, 5000, 12345, 29999, 30000, 100000, 1000000)) {
            val range = EconomyCatalog.recordCoins(best)
            assertTrue("пустой диапазон при $best", range.first <= range.last)
            repeat(50) {
                val coin = rewards(best).first { it.type == BonusType.COIN }.count
                assertEquals("кратность при $best", 0, coin % 5)
                assertTrue("$coin вне $range при $best", coin in range)
            }
        }
    }

    @Test
    fun `награда за рекорд не падает с ростом рекорда`() {
        var previous = EconomyCatalog.recordCoins(0)
        for (best in 0..100000 step 500) {
            val range = EconomyCatalog.recordCoins(best)
            assertTrue("падение при $best", range.first >= previous.first && range.last >= previous.last)
            previous = range
        }
    }

    @Test
    fun `бонус за рекорд только с 5000 очков и не монета`() {
        val rewards = GetRecordRewardsUseCase()
        assertEquals(1, rewards(4999).size)
        repeat(50) {
            val list = rewards(5000)
            assertEquals(2, list.size)
            assertTrue(list[1].type != BonusType.COIN)
        }
    }

    @Test
    fun `цены магазина и стартовые значения согласованы`() {
        val prices = ShopItemCatalog.all.filter { it.bonusType != null }.associate { it.bonusType to it.price }
        assertEquals(setOf(BonusType.ROTATE, BonusType.FREEZE, BonusType.LUCKY, BonusType.DRAFT), prices.keys)
        assertTrue(prices.values.all { it > 0 })
        assertEquals("id магазина уникальны", ShopItemCatalog.all.size, ShopItemCatalog.all.map { it.id }.toSet().size)
        assertTrue(EconomyCatalog.START_COINS >= 0)
        assertTrue(EconomyCatalog.DAILY_COINS.all { it % 5 == 0 })
        assertEquals(EconomyCatalog.DAILY_COINS.sorted(), EconomyCatalog.DAILY_COINS)
    }

    @Test
    fun `партия переживает перезапуск игры`() = runBlocking {
        val store = MemoryDataStore()
        val first = TestWorld(store)
        first.setGame { copy(coins = 123, totalScore = 456).withBonuses(1, 2, 3, 4).withDice(2, 5) }
        first.place(3, 3)
        first.games.saveGame(first.game())

        val reopened = TestWorld(store).game()
        assertEquals(123, reopened.coins)
        assertEquals(456, reopened.totalScore)
        assertEquals(3, reopened.bonusCount(BonusType.LUCKY))
        assertTrue(reopened.boardItem.board[3][3])
        assertTrue(reopened.boardItem.board[4][7])
        assertFalse(reopened.boardItem.board[5][3])
        assertEquals(first.game().reviveSnapshot?.totalScore, reopened.reviveSnapshot?.totalScore)
    }

    @Test
    fun `старое или испорченное сохранение не роняет игру`() = runBlocking {
        val broken = TestWorld(MemoryDataStore(mapOf("game_statistic_json_v2" to "{не json")))
        assertEquals(EconomyCatalog.START_COINS, broken.game().coins)

        val old = TestWorld(MemoryDataStore(mapOf("game_statistic_json_v2" to """{"coins":55}""")))
        val g = old.game()
        assertEquals(55, g.coins)
        assertEquals(11, g.boardItem.board.size)
        assertEquals(4, g.bonuses.size)
    }

    @Test
    fun `неизвестный бонус в сохранении не ломает запас`() = runBlocking {
        val json = """{"coins":5,"bonuses":[{"type":"ROTATE","count":7},{"type":"REMOVED_BONUS","count":3}]}"""
        val g = TestWorld(MemoryDataStore(mapOf("game_statistic_json_v2" to json))).game()
        assertEquals(7, g.bonusCount(BonusType.ROTATE))
        assertEquals(4, g.bonuses.size)
    }
}

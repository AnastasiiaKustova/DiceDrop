package com.example.dicedrop

import com.example.dicedrop.domain.catalog.EconomyCatalog
import com.example.dicedrop.domain.entity.BoardItem
import com.example.dicedrop.domain.entity.BonusItem
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.domain.entity.DiceItem
import com.example.dicedrop.domain.entity.statistic.PlayerStat
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

// Ход целиком: установка блока, линии, очки, монеты, рекорд, отмена хода, новая партия
class GamePlayTest {

    @Test
    fun `установка блока занимает клетки и убирает кубики`() = runBlocking {
        val w = TestWorld()
        w.setDice(2, 3)
        val placed = assertNotNullResult(w.place(1, 1))
        assertTrue(placed.boardItem.board[1][1])
        assertTrue(placed.boardItem.board[2][3])
        assertFalse(placed.boardItem.board[3][1])
        assertEquals(DiceItem.empty(), placed.dice)
        assertEquals(1, placed.moves)
        assertEquals(6, placed.lastPlaced.size)
    }

    @Test
    fun `нельзя поставить блок на занятое место или за край`() = runBlocking {
        val w = TestWorld()
        w.setDice(2, 2)
        w.place(0, 0)
        w.setDice(1, 1)
        assertNull(w.place(0, 0))
        w.setDice(6, 6)
        assertNull(w.place(6, 0))
        assertEquals(1, w.game().moves)
    }

    @Test
    fun `нельзя поставить пустой блок, когда кубиков нет`() = runBlocking {
        val w = TestWorld()
        w.setDice(1, 1)
        w.place(0, 0)
        val snapshotAfterMove = w.game().reviveSnapshot
        assertNull("блока нет, ход не должен засчитаться", w.place(5, 5))
        assertEquals(1, w.game().moves)
        assertEquals(snapshotAfterMove, w.game().reviveSnapshot)
    }

    @Test
    fun `одна линия даёт 100 очков и монеты за линию`() = runBlocking {
        val w = TestWorld()
        val before = w.game().coins
        w.setGame { copy(boardItem = rowAlmostFull(0, freeCol = 10)).withDice(1, 1) }
        val result = w.placeAndClear(0, 10)
        assertEquals(1, result.clearedLinesCount)
        assertEquals(100, result.points)
        assertEquals(100, w.game().totalScore)
        assertEquals(before + EconomyCatalog.COINS_PER_LINE + EconomyCatalog.BOARD_CLEAR_COINS, w.game().coins)
        assertFalse(w.game().boardItem.board[0][0])
    }

    @Test
    fun `две линии одним ходом считаются с комбо`() = runBlocking {
        val w = TestWorld()
        val board = fullBoardExcept(5 to 5).copy(
            board = BoardItem.default().board.mapIndexed { r, line ->
                BooleanArray(line.size) { c -> (r == 5 || c == 5) && !(r == 5 && c == 5) }
            }
        )
        w.setGame { copy(boardItem = board).withDice(1, 1) }
        val result = w.placeAndClear(5, 5)
        assertEquals(2, result.clearedLinesCount)
        assertEquals(2, result.combo)
        assertEquals(400, result.points)
    }

    @Test
    fun `комбо копится на очистках подряд и сбрасывается ходом без очистки`() = runBlocking {
        val w = TestWorld()
        // Клетка, которая держит поле непустым, чтобы не срабатывала полная очистка
        val keep = BoardItem.default().copy(
            board = BoardItem.default().board.mapIndexed { r, line ->
                BooleanArray(line.size) { c -> r == 10 && c == 0 }
            }
        )
        w.setGame { copy(boardItem = rowAlmostFull(0, 10, keep)).withDice(1, 1) }
        assertEquals(1, w.placeAndClear(0, 10).combo)

        w.setGame { copy(boardItem = rowAlmostFull(0, 10, boardItem)).withDice(1, 1) }
        val second = w.placeAndClear(0, 10)
        assertEquals(2, second.combo)
        assertEquals(200, second.points)

        w.setDice(1, 1)
        assertEquals(0, w.placeAndClear(5, 5).combo)
        assertEquals(0, w.game().combo)
    }

    @Test
    fun `полная очистка поля считается и даёт монеты`() = runBlocking {
        val w = TestWorld()
        w.setGame { copy(boardItem = rowAlmostFull(3, 3)).withDice(1, 1) }
        val coins = w.game().coins
        w.placeAndClear(3, 3)
        assertEquals(1, w.game().boardClears)
        assertEquals(coins + EconomyCatalog.COINS_PER_LINE + EconomyCatalog.BOARD_CLEAR_COINS, w.game().coins)
        assertEquals(1L, w.stat(PlayerStat.BOARD_CLEARED))
    }

    @Test
    fun `в первой партии окна рекорда нет`() = runBlocking {
        val w = TestWorld()
        w.setGame { copy(boardItem = rowAlmostFull(0, 10)).withDice(1, 1) }
        w.placeAndClear(0, 10)
        assertFalse(w.game().isNewRecord)
    }

    @Test
    fun `побитый рекорд показывает окно, а награда выдаётся один раз`() = runBlocking {
        val w = TestWorld()
        w.setGame { copy(bestScore = 50, boardItem = rowAlmostFull(0, 10)).withDice(1, 1) }
        w.placeAndClear(0, 10)
        assertTrue(w.game().isNewRecord)
        assertEquals("рекорд обновляется, когда игрок заберёт награду", 50, w.game().bestScore)

        val coins = w.game().coins
        w.confirmRecord(listOf(BonusItem(BonusType.COIN, 40), BonusItem(BonusType.LUCKY, 1)))
        val lucky = w.game().bonusCount(BonusType.LUCKY)
        assertEquals(coins + 40, w.game().coins)
        assertEquals(100, w.game().bestScore)
        assertTrue(w.game().isRecordAchieved)
        assertFalse(w.game().isNewRecord)

        w.confirmRecord(listOf(BonusItem(BonusType.COIN, 40), BonusItem(BonusType.LUCKY, 1)))
        assertEquals(coins + 40, w.game().coins)
        assertEquals(lucky, w.game().bonusCount(BonusType.LUCKY))
    }

    @Test
    fun `после рекорда он растёт вместе со счётом без новых окон`() = runBlocking {
        val w = TestWorld()
        w.setGame {
            copy(bestScore = 100, totalScore = 100, isRecordAchieved = true, boardItem = rowAlmostFull(0, 10))
                .withDice(1, 1)
        }
        w.placeAndClear(0, 10)
        assertEquals(200, w.game().bestScore)
        assertFalse(w.game().isNewRecord)
    }

    @Test
    fun `отмена хода за рекламу возвращает поле, блок, счёт и комбо`() = runBlocking {
        val w = TestWorld()
        w.setGame { copy(totalScore = 500, combo = 2).withDice(2, 2) }
        w.place(0, 0)
        w.setGame { copy(isGameOver = true) }

        w.revive()
        val g = w.game()
        assertFalse(g.boardItem.board[0][0])
        assertEquals(DiceItem(2, 2), g.dice)
        assertEquals(500, g.totalScore)
        assertEquals(2, g.combo)
        assertFalse(g.isGameOver)
        assertEquals(0, g.moves)
        assertNull(g.reviveSnapshot)
    }

    @Test
    fun `отмена хода не даёт заново получить монеты за тот же ход`() = runBlocking {
        val w = TestWorld()
        w.setGame { copy(boardItem = rowAlmostFull(0, 10)).withDice(1, 1) }
        val coins = w.game().coins
        w.placeAndClear(0, 10)
        w.revive()
        assertEquals("монеты за отменённый ход должны вернуться", coins, w.game().coins)
        assertEquals(0, w.game().coinsEarned)
        assertEquals(0, w.game().boardClears)
    }

    @Test
    fun `отмена хода не трогает монеты из покупок и подарков`() = runBlocking {
        val w = TestWorld()
        w.setGame { copy(coins = 100, boardItem = rowAlmostFull(0, 10)).withBonuses().withDice(1, 1) }
        w.placeAndClear(0, 10)
        val earned = w.game().coins - 100
        w.buy(BonusType.ROTATE, price = 5)
        w.daily()
        val beforeRevive = w.game().coins
        w.revive()
        assertEquals(beforeRevive - earned, w.game().coins)
        assertEquals("покупка остаётся", 1, w.game().bonusCount(BonusType.ROTATE))
    }

    @Test
    fun `отмена хода не уводит монеты в минус, если они уже потрачены`() = runBlocking {
        val w = TestWorld()
        w.setGame { copy(coins = 0, boardItem = rowAlmostFull(0, 10)).withBonuses().withDice(1, 1) }
        w.placeAndClear(0, 10)
        w.setGame { copy(coins = 2) }
        w.revive()
        assertEquals(0, w.game().coins)
    }

    @Test
    fun `отмена хода без очистки не забирает монет`() = runBlocking {
        val w = TestWorld()
        w.setGame { copy(coins = 50).withDice(1, 1) }
        w.placeAndClear(5, 5)
        w.revive()
        assertEquals(50, w.game().coins)
    }

    @Test
    fun `старое сохранение без новых полей снимка отменяется без ошибок`() = runBlocking {
        val w = TestWorld()
        w.setDice(1, 1)
        w.place(0, 0)
        val json = com.google.gson.Gson().toJson(w.game())
            .replace(""","boardClears":0,"moveCoins":0""", "")
        val old = TestWorld(MemoryDataStore(mapOf("game_statistic_json_v2" to json)))
        val coins = old.game().coins
        old.revive()
        assertEquals(coins, old.game().coins)
    }

    @Test
    fun `отмена хода не опускает рекорд`() = runBlocking {
        val w = TestWorld()
        // Рекорд уже побит и растёт вместе со счётом; ход с большим комбо поднимает его до 1600
        w.setGame {
            copy(bestScore = 1000, totalScore = 1000, combo = 5, isRecordAchieved = true, boardItem = rowAlmostFull(0, 10))
                .withDice(1, 1)
        }
        w.placeAndClear(0, 10)
        assertEquals(1600, w.game().bestScore)

        w.revive()
        assertEquals(1000, w.game().totalScore)

        // После отмены игрок ходит иначе и комбо обрывается: следующая линия даёт всего 100 очков
        w.setGame { copy(combo = 0, boardItem = rowAlmostFull(0, 10)).withDice(1, 1) }
        w.placeAndClear(0, 10)
        assertEquals(1100, w.game().totalScore)
        assertTrue("рекорд упал с 1600 до ${w.game().bestScore}", w.game().bestScore >= 1600)
    }

    @Test
    fun `после отмены рекорд снова растёт, когда счёт его превысит, без нового окна и награды`() = runBlocking {
        val w = TestWorld()
        w.setGame {
            copy(bestScore = 1000, totalScore = 1000, combo = 5, isRecordAchieved = true, boardItem = rowAlmostFull(0, 10))
                .withDice(1, 1)
        }
        w.placeAndClear(0, 10)
        assertEquals(1600, w.game().bestScore)
        w.revive()

        // Счёт 1000 + 500 ещё ниже рекорда 1600: рекорд на месте
        w.setGame { copy(combo = 4, boardItem = rowAlmostFull(0, 10)).withDice(1, 1) }
        w.placeAndClear(0, 10)
        assertEquals(1500, w.game().totalScore)
        assertEquals(1600, w.game().bestScore)

        // Счёт 1500 + 600 превышает рекорд: он растёт вместе со счётом
        w.setGame { copy(combo = 5, boardItem = rowAlmostFull(0, 10)).withDice(1, 1) }
        w.placeAndClear(0, 10)
        assertEquals(2100, w.game().totalScore)
        assertEquals(2100, w.game().bestScore)
        assertFalse(w.game().isNewRecord)
    }

    @Test
    fun `отмена без снимка просто даёт новый бросок`() = runBlocking {
        val w = TestWorld()
        w.setGame { copy(isGameOver = true).withDice(6, 6) }
        w.revive()
        assertEquals(DiceItem.empty(), w.game().dice)
        assertFalse(w.game().isGameOver)
    }

    @Test
    fun `удвоение монет работает один раз и не срабатывает без заработка`() = runBlocking {
        val w = TestWorld()
        val start = w.game().coins
        w.doubleCoins()
        assertEquals(start, w.game().coins)

        w.setGame { copy(coinsEarned = 30, coins = start + 30) }
        w.doubleCoins()
        assertEquals(start + 60, w.game().coins)
        w.doubleCoins()
        assertEquals(start + 60, w.game().coins)
    }

    @Test
    fun `новая партия сохраняет монеты, бонусы и рекорд, остальное сбрасывает`() = runBlocking {
        val w = TestWorld()
        w.setGame {
            copy(coins = 777, totalScore = 900, bestScore = 500, combo = 3, moves = 9, isRecordAchieved = true)
                .withBonuses(rotate = 1, freeze = 2, lucky = 3, draft = 4)
                .withDice(3, 3)
        }
        w.restart()
        val g = w.game()
        assertEquals(777, g.coins)
        assertEquals(900, g.bestScore)
        assertEquals(0, g.totalScore)
        assertEquals(0, g.combo)
        assertEquals(0, g.moves)
        assertFalse(g.isRecordAchieved)
        assertEquals(DiceItem.empty(), g.dice)
        assertEquals(3, g.bonusCount(BonusType.LUCKY))
        assertTrue(g.boardItem.board.all { row -> row.none { it } })
    }

    @Test
    fun `партия считается сыгранной только если был ход`() = runBlocking {
        val w = TestWorld()
        w.restart()
        assertEquals(0L, w.stat(PlayerStat.GAMES_PLAYED))
        w.setDice(1, 1)
        w.place(0, 0)
        w.restart()
        assertEquals(1L, w.stat(PlayerStat.GAMES_PLAYED))
    }

    @Test
    fun `статистика хода считается`() = runBlocking {
        val w = TestWorld()
        w.setDice(1, 1)
        w.place(0, 0)
        assertEquals(1L, w.stat(PlayerStat.TINY_BLOCKS_PLACED))
        assertEquals(1L, w.stat(PlayerStat.MAX_BLOCK_AREA))

        w.setDice(6, 6)
        w.place(1, 1)
        assertEquals(1L, w.stat(PlayerStat.TINY_BLOCKS_PLACED))
        assertEquals(36L, w.stat(PlayerStat.MAX_BLOCK_AREA))
        assertEquals(2L, w.stat(PlayerStat.MAX_MOVES_IN_GAME))
    }

    @Test
    fun `линия блоком 1 на 1 считается для снайпера, другим блоком нет`() = runBlocking {
        val w = TestWorld()
        w.setGame { copy(boardItem = rowAlmostFull(0, 10)).withDice(1, 1) }
        w.placeAndClear(0, 10)
        assertEquals(1L, w.stat(PlayerStat.LINES_BY_TINY_BLOCK))

        w.setGame { copy(boardItem = rowAlmostFull(0, 10)).withDice(2, 1) }
        w.placeAndClear(0, 10)
        assertEquals(1L, w.stat(PlayerStat.LINES_BY_TINY_BLOCK))
    }

    @Test
    fun `второй шанс засчитывается на третьем ходу после отмены и один раз`() = runBlocking {
        val w = TestWorld()
        w.setGame { copy(movesAfterRevive = 0) }
        repeat(2) { i ->
            w.setDice(1, 1)
            w.place(0, i)
        }
        assertEquals(0L, w.stat(PlayerStat.REVIVES_SURVIVED))
        repeat(3) { i ->
            w.setDice(1, 1)
            w.place(1, i)
        }
        assertEquals(1L, w.stat(PlayerStat.REVIVES_SURVIVED))
    }

    private fun <T> assertNotNullResult(value: T?): T {
        assertNotNull(value)
        return value!!
    }
}

package com.example.dicedrop

import com.example.dicedrop.core.NotEnoughBonusException
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.domain.entity.DiceItem
import com.example.dicedrop.domain.entity.DiscardTarget
import com.example.dicedrop.domain.entity.Slot
import com.example.dicedrop.domain.entity.TrayDie
import com.example.dicedrop.domain.entity.statistic.PlayerStat
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

// Бонусы, конец игры и подсказка «блок не помещается»
class BonusesTest {

    @Test
    fun `перевернуть меняет стороны блока и списывает бонус`() = runBlocking {
        val w = TestWorld()
        w.setGame { withBonuses(rotate = 2).withDice(2, 5) }
        w.useBonus(BonusType.ROTATE)
        assertEquals(DiceItem(5, 2), w.game().dice)
        assertEquals(1, w.game().bonusCount(BonusType.ROTATE))
    }

    @Test
    fun `бонус без остатка не срабатывает`() = runBlocking {
        val w = TestWorld()
        w.setGame { withBonuses().withDice(2, 5) }
        assertThrows(NotEnoughBonusException::class.java) { runBlocking { w.useBonus(BonusType.ROTATE) } }
        assertEquals(DiceItem(2, 5), w.game().dice)
    }

    @Test
    fun `без кубиков бонус не тратится`() = runBlocking {
        val w = TestWorld()
        w.setGame { withBonuses(rotate = 1, freeze = 1, draft = 1) }
        w.useBonus(BonusType.ROTATE)
        w.useBonus(BonusType.FREEZE)
        w.useBonus(BonusType.DRAFT)
        assertEquals(1, w.game().bonusCount(BonusType.ROTATE))
        assertEquals(1, w.game().bonusCount(BonusType.FREEZE))
        assertEquals(1, w.game().bonusCount(BonusType.DRAFT))
    }

    @Test
    fun `перевернуть при третьем кубике не тратит бонус впустую`() = runBlocking {
        val w = TestWorld()
        w.setGame { withBonuses(rotate = 1, draft = 1).withDice(2, 5) }
        w.useBonus(BonusType.DRAFT)
        w.useBonus(BonusType.ROTATE)
        assertEquals(1, w.game().bonusCount(BonusType.ROTATE))
    }

    @Test
    fun `заморозка оставляет те же кубики на следующий бросок`() = runBlocking {
        val w = TestWorld()
        w.setGame { withBonuses(freeze = 1).withDice(4, 3) }
        w.useBonus(BonusType.FREEZE)
        assertEquals(0, w.game().bonusCount(BonusType.FREEZE))
        w.place(0, 0)
        w.roll(DiceItem(1, 1))
        assertEquals(DiceItem(4, 3), w.game().dice)
    }

    @Test
    fun `заморозка держится один бросок`() = runBlocking {
        val w = TestWorld()
        w.setGame { withBonuses(freeze = 1).withDice(4, 3) }
        w.useBonus(BonusType.FREEZE)
        w.place(0, 0)
        w.roll(DiceItem(1, 1))
        w.place(6, 6)
        w.roll(DiceItem(2, 2))
        assertEquals(DiceItem(2, 2), w.game().dice)
    }

    @Test
    fun `повторная заморозка не тратит бонус`() = runBlocking {
        val w = TestWorld()
        w.setGame { withBonuses(freeze = 2).withDice(4, 3) }
        w.useBonus(BonusType.FREEZE)
        w.useBonus(BonusType.FREEZE)
        assertEquals(1, w.game().bonusCount(BonusType.FREEZE))
    }

    @Test
    fun `счастливый меняет нужный кубик и списывает бонус`() = runBlocking {
        val w = TestWorld()
        w.setGame { withBonuses(lucky = 2).withDice(6, 5) }
        w.useBonus.setFace(1, Slot.A)
        assertEquals("кубик A - ширина блока", DiceItem(6, 1), w.game().dice)
        assertEquals(1, w.game().bonusCount(BonusType.LUCKY))
        w.useBonus.setFace(2, Slot.B)
        assertEquals("кубик B - высота блока", DiceItem(2, 1), w.game().dice)
        assertEquals(0, w.game().bonusCount(BonusType.LUCKY))
    }

    @Test
    fun `счастливый без кубиков не возвращает поставленный блок`() = runBlocking {
        val w = TestWorld()
        w.setGame { withBonuses(lucky = 1).withDice(2, 2) }
        w.place(0, 0)
        w.useBonus.setFace(1, Slot.A)
        assertEquals("блок уже поставлен, нового быть не должно", DiceItem.empty(), w.game().dice)
        assertEquals(1, w.game().bonusCount(BonusType.LUCKY))
    }

    @Test
    fun `третий кубик добавляется один раз и блокирует установку`() = runBlocking {
        val w = TestWorld()
        w.setGame { withBonuses(draft = 2).withDice(2, 5) }
        w.useBonus(BonusType.DRAFT)
        w.useBonus(BonusType.DRAFT)
        assertEquals(1, w.game().bonusCount(BonusType.DRAFT))
        assertNull(w.place(0, 0))
    }

    @Test
    fun `сброс кубика собирает блок из двух оставшихся`() = runBlocking {
        val w = TestWorld()
        // Кубик A - ширина, кубик B - высота
        suspend fun start(spare: Int) {
            w.setGame { withBonuses(draft = 1).withDice(2, 5) }
            w.useBonus(BonusType.DRAFT)
            w.rollSpare(spare)
        }
        start(3)
        w.discard(DiscardTarget.SPARE)
        assertEquals(DiceItem(2, 5), w.game().dice)
        assertNull(w.game().tray.spare)

        start(3)
        w.discard(DiscardTarget.A)
        assertEquals("сбросили ширину 5: остались 2 и 3", DiceItem(3, 2), w.game().dice)

        start(3)
        w.discard(DiscardTarget.B)
        assertEquals("сбросили высоту 2: остались 5 и 3", DiceItem(3, 5), w.game().dice)
    }

    @Test
    fun `после сброса блок снова можно поставить`() = runBlocking {
        val w = TestWorld()
        w.setGame { withBonuses(draft = 1).withDice(2, 5) }
        w.useBonus(BonusType.DRAFT)
        w.rollSpare(3)
        w.discard(DiscardTarget.SPARE)
        assertNotNull(w.place(0, 0))
    }

    @Test
    fun `пока третий кубик катится, сбросить нельзя и блок не ломается`() = runBlocking {
        val w = TestWorld()
        w.setGame { withBonuses(draft = 1).withDice(2, 5) }
        w.useBonus(BonusType.DRAFT)
        w.discard(DiscardTarget.A)
        assertEquals(DiceItem(2, 5), w.game().dice)
        assertEquals(TrayDie(null), w.game().tray.spare)
    }

    @Test
    fun `третий кубик тем же числом при дубле даёт утешительное достижение`() = runBlocking {
        val w = TestWorld()
        w.setGame { withBonuses(draft = 1).withDice(4, 4) }
        w.useBonus(BonusType.DRAFT)
        w.rollSpare(4)
        assertEquals(1L, w.stat(PlayerStat.DRAFT_TRIPLE))
    }

    @Test
    fun `все четыре бонуса засчитываются один раз за партию`() = runBlocking {
        val w = TestWorld()
        w.setGame { withBonuses(rotate = 5, freeze = 5, lucky = 5, draft = 5).withDice(2, 3) }
        w.useBonus(BonusType.ROTATE)
        w.useBonus(BonusType.FREEZE)
        w.useBonus.setFace(1, Slot.A)
        assertEquals(0L, w.stat(PlayerStat.ALL_BONUSES_GAMES))
        w.useBonus(BonusType.DRAFT)
        assertEquals(1L, w.stat(PlayerStat.ALL_BONUSES_GAMES))
        w.rollSpare(2)
        w.discard(DiscardTarget.SPARE)
        w.useBonus(BonusType.ROTATE)
        assertEquals(1L, w.stat(PlayerStat.ALL_BONUSES_GAMES))
    }

    @Test
    fun `блок помещается - конца игры и подсказки нет`() = runBlocking {
        val w = TestWorld()
        w.setGame { withBonuses().withDice(6, 6) }
        val g = w.checkGameOver()
        assertFalse(g.isGameOver)
        assertTrue(g.rescueBonuses.isEmpty())
    }

    @Test
    fun `без мест и бонусов игра окончена, проигрыш считается один раз`() = runBlocking {
        val w = TestWorld()
        w.setGame { copy(boardItem = fullBoardExcept(0 to 0)).withBonuses().withDice(2, 2) }
        assertTrue(w.checkGameOver().isGameOver)
        w.checkGameOver()
        assertEquals(1L, w.stat(PlayerStat.GAMES_LOST))
    }

    @Test
    fun `полное поле - конец игры даже с бонусами`() = runBlocking {
        val w = TestWorld()
        w.setGame { copy(boardItem = fullBoardExcept()).withBonuses(9, 9, 9, 9).withDice(1, 1) }
        assertTrue(w.checkGameOver().isGameOver)
    }

    @Test
    fun `перевернуть спасает, только если повёрнутый блок встаёт`() = runBlocking {
        val w = TestWorld()
        val slot = fullBoardExcept(4 to 4, 5 to 4, 6 to 4)
        w.setGame { copy(boardItem = slot).withBonuses(rotate = 1).withDice(1, 3) }
        assertEquals(listOf(BonusType.ROTATE), w.checkGameOver().rescueBonuses)

        w.setGame { withBonuses().withDice(1, 3) }
        assertTrue(w.checkGameOver().isGameOver)
    }

    @Test
    fun `счастливый спасает блок 6 на 6 в прорезь 6 на 1`() = runBlocking {
        val w = TestWorld()
        val slot = fullBoardExcept(*Array(6) { 2 to 3 + it })
        w.setGame { copy(boardItem = slot).withBonuses(lucky = 1).withDice(6, 6) }
        val g = w.checkGameOver()
        assertFalse(g.isGameOver)
        assertEquals(listOf(BonusType.LUCKY), g.rescueBonuses)
    }

    @Test
    fun `только два бонуса замены вместе спасают от дырки 1 на 1`() = runBlocking {
        val w = TestWorld()
        val hole = fullBoardExcept(7 to 7)
        w.setGame { copy(boardItem = hole).withBonuses(lucky = 1).withDice(6, 6) }
        assertTrue("одного счастливого мало", w.checkGameOver().isGameOver)

        w.setGame { withBonuses(lucky = 1, draft = 1).withDice(6, 6) }
        val g = w.checkGameOver()
        assertFalse(g.isGameOver)
        assertEquals(listOf(BonusType.LUCKY, BonusType.DRAFT), g.rescueBonuses)
    }

    @Test
    fun `смена кубиков сразу сбрасывает старую подсказку о спасении`() = runBlocking {
        val w = TestWorld()
        val slot = fullBoardExcept(*Array(6) { 2 to 3 + it })
        w.setGame { copy(boardItem = slot).withBonuses(rotate = 1, lucky = 2).withDice(6, 6) }
        assertTrue(w.checkGameOver().rescueBonuses.isNotEmpty())
        w.useBonus.setFace(1, Slot.B)
        assertTrue("счастливый: окно не должно мелькать", w.game().rescueBonuses.isEmpty())

        w.setGame { copy(boardItem = slot).withDice(6, 1) }
        assertTrue(w.checkGameOver().rescueBonuses.isNotEmpty())
        w.useBonus(BonusType.ROTATE)
        assertTrue("перевернуть: окно не должно мелькать", w.game().rescueBonuses.isEmpty())
    }

    @Test
    fun `после конца игры бросок ничего не меняет`() = runBlocking {
        val w = TestWorld()
        w.setGame { copy(isGameOver = true).withDice(6, 6) }
        w.roll(DiceItem(1, 1))
        assertEquals(DiceItem(6, 6), w.game().dice)
    }
}

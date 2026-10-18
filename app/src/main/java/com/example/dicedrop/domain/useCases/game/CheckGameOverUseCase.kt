package com.example.dicedrop.domain.useCases.game

import com.example.dicedrop.domain.engine.BoardEngine.isCanPlaceNewBlock
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.domain.entity.GameStatisticItem
import com.example.dicedrop.domain.repository.GameRepository
import javax.inject.Inject

class CheckGameOverUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(): GameStatisticItem {
        val game = repository.getCurrentGame()

        val h = game.dice.dice1
        val w = game.dice.dice2
        fun count(t: BonusType) = game.bonuses.firstOrNull { it.type == t }?.count ?: 0
        val canRotate = count(BonusType.ROTATE) > 0

        fun fits(h: Int, w: Int): Boolean =
            game.boardItem.isCanPlaceNewBlock(h, w) || (canRotate && game.boardItem.isCanPlaceNewBlock(w, h))

        // Третий кубик еще не сброшен: сброс A дает блок h=spare × w=B, сброс B - h=spare × w=A
        val spare = game.tray.spare?.value
        val canPlace = fits(h, w) || (spare != null && (fits(spare, h) || fits(spare, w)))

        // Каждый Lucky или Draft позволяет заменить один кубик. Для Draft берем лучший исход,
        // а грань 1 проверять достаточно: если не влезает 1, не влезет и больше
        val replaceCount = count(BonusType.LUCKY) + count(BonusType.DRAFT)
        val canSurvive = when {
            replaceCount >= 2 -> fits(1, 1)
            count(BonusType.LUCKY) > 0 -> fits(h, 1) || fits(1, w)
            count(BonusType.DRAFT) > 0 -> fits(1, h) || fits(1, w)
            else -> false
        }

        val updated = game.copy(isGameOver = !canPlace && !canSurvive)

        repository.saveGame(updated)
        return updated
    }
}
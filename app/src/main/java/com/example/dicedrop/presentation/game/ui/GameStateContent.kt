import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.domain.entity.DiceItem
import com.example.dicedrop.domain.entity.DiscardTarget
import com.example.dicedrop.domain.entity.DraggingType
import com.example.dicedrop.domain.entity.GameStatisticItem
import com.example.dicedrop.domain.entity.Slot
import com.example.dicedrop.presentation.game.GameComponent
import com.example.dicedrop.presentation.game.GameStore
import com.example.dicedrop.presentation.game.ui.board.BottomBoard
import com.example.dicedrop.presentation.game.ui.board.ComboPopup
import com.example.dicedrop.presentation.game.ui.board.DrawPlayingField
import com.example.dicedrop.presentation.game.ui.bottompanel.GameBottomPanel
import com.example.dicedrop.presentation.game.ui.bottompanel.getByType
import com.example.dicedrop.presentation.game.ui.drag.BoardUiState
import com.example.dicedrop.presentation.game.ui.drag.GameUiState
import com.example.dicedrop.presentation.game.ui.drag.GameUiState.Companion.getGhost
import com.example.dicedrop.presentation.game.ui.header.GameHeader
import com.example.dicedrop.presentation.game.ui.popups.GameOverDialog
import com.example.dicedrop.presentation.game.ui.popups.LuckyChoiceDialog
import com.example.dicedrop.presentation.game.ui.popups.NewRecordDialog
import com.example.dicedrop.presentation.settings.ui.DrawPause
import com.example.dicedrop.presentation.ui.theme.DarkBlue
import com.example.dicedrop.presentation.ui.theme.White

@Composable
fun GameStateContent(
    state: GameStore.State.GameState,
    clearedRows: List<Int>,
    clearedCols: List<Int>,
    comboPopup: ComboPopup?,
    trophyHighlighted: Boolean,
    rewardsToClaim: Int,
    component: GameComponent
) {
    // "Счастливый": сначала игрок выбирает в шторке грань, затем кубик, которому её поставить
    var showLuckySheet by rememberSaveable { mutableStateOf(false) }
    var luckyFace by rememberSaveable { mutableStateOf<Int?>(null) }
    var showPause by rememberSaveable { mutableStateOf(false) }

    fun cancelLucky() {
        showLuckySheet = false
        luckyFace = null
    }

    // Системная "Назад" сначала закрывает открытые окна и режимы, а не выходит из игры
    BackHandler(enabled = showPause || showLuckySheet || luckyFace != null) {
        if (showPause) showPause = false else cancelLucky()
    }

    val configuration = LocalConfiguration.current

    val isLandscape = when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> true
        else -> false
    }

    Box(modifier = Modifier.fillMaxSize()) {

        val gameState = when (state) {
            is GameStore.State.GameState.Loaded -> state
            is GameStore.State.GameState.NewRecord -> state.prevState
            is GameStore.State.GameState.Over -> state.prevState
            else -> null
        }

        if (gameState != null) {
            val game = gameState.gameStatistic
            GameScreen(
                gameStatistic = game,
                clearedRows = clearedRows,
                clearedCols = clearedCols,
                comboPopup = comboPopup,
                isLandscape = isLandscape,
                trophyHighlighted = trophyHighlighted,
                rewardsToClaim = rewardsToClaim,
                luckyFace = luckyFace,
                onShopClick = component::onShopClick,
                onAchievementsClick = component::onAchievementClick,
                onPauseClick = { showPause = true },
                onRollEnd = component::onRollEnd,
                onUseBonusClick = component::onBonusClick,
                onBuyBonusClick = component::onBuyBonusClick,
                onPlaceBlock = component::placeBlock,
                onFreezeAll = component::onFreezeAll,
                onDiscardClick = component::onDiscardClick,
                onSpareRolled = component::onSpareRolled,
                onLuckyClick = {
                    if (luckyFace != null) cancelLucky() else showLuckySheet = true
                },
                onLuckyDieClick = { slot ->
                    luckyFace?.let { component.onLuckyFace(it, slot) }
                    cancelLucky()
                }
            )

            if (showLuckySheet) {
                LuckyChoiceDialog(
                    onPick = { face ->
                        luckyFace = face
                        showLuckySheet = false
                    },
                    onCancel = { cancelLucky() }
                )
            }
        } else if (state == GameStore.State.GameState.Loading) {
            Loading()
        }

        when (state) {
            is GameStore.State.GameState.NewRecord -> {
                NewRecordDialog(
                    record = state.score,
                    previousRecord = state.prevState.gameStatistic.bestScore,
                    rewardsList = state.rewardsList,
                    confirmClick = { component.onConfirmNewRecord(state.rewardsList) }
                )
            }

            is GameStore.State.GameState.Over -> {
                GameOverDialog(
                    gameScore = state.score,
                    bestScore = state.prevState.gameStatistic.bestScore,
                    canUndo = state.prevState.gameStatistic.reviveSnapshot != null,
                    onRestartClick = {
                        cancelLucky()
                        component.restartGame()
                    },
                    onWatchAdsClick = component::onWatchAdsClick
                )
            }

            else -> Unit
        }

        if (showPause) {
            DrawPause(
                onBackClick = { showPause = false },
                onRestartClick = {
                    showPause = false
                    cancelLucky()
                    component.restartGame()
                },
                onMenuClick = {
                    showPause = false
                    component.onBackPressed()
                }
            )
        }
    }
}

@Composable
private fun GameScreen(
    gameStatistic: GameStatisticItem,
    clearedRows: List<Int>,
    clearedCols: List<Int>,
    comboPopup: ComboPopup?,
    isLandscape: Boolean,
    trophyHighlighted: Boolean,
    rewardsToClaim: Int,
    luckyFace: Int?,
    onShopClick: () -> Unit,
    onAchievementsClick: () -> Unit,
    onPauseClick: () -> Unit,
    onRollEnd: (result: DiceItem) -> Unit,
    onUseBonusClick: (BonusType) -> Unit,
    onBuyBonusClick: (BonusType) -> Unit,
    onPlaceBlock: (r: Int, c: Int) -> Unit,
    onFreezeAll: () -> Unit,
    onDiscardClick: (DiscardTarget) -> Unit,
    onSpareRolled: (Int) -> Unit,
    onLuckyClick: () -> Unit,
    onLuckyDieClick: (Slot) -> Unit,
) {
    val spareActive = gameStatistic.tray.spare != null
    val diceReady = gameStatistic.dice != DiceItem.empty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlue),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val uiStates = remember { GameUiState.getList() }
        val boardState = remember { BoardUiState() }

        GameHeader(
            totalScore = gameStatistic.totalScore,
            bestScore = gameStatistic.bestScore,
            coins = gameStatistic.coins,
            combo = gameStatistic.combo,
            trophyHighlighted = trophyHighlighted,
            rewardsToClaim = rewardsToClaim,
            onPauseClick = onPauseClick,
            onShopClick = onShopClick,
            onAchievementsClick = onAchievementsClick
        )

        val field = @Composable { fieldModifier: Modifier, trayModifier: Modifier, panelModifier: Modifier ->
            DrawPlayingField(
                modifier = fieldModifier,
                boardItem = gameStatistic.boardItem,
                clearedRows = clearedRows,
                clearedCols = clearedCols,
                comboPopup = comboPopup,
                boardState = boardState,
                diceItem = gameStatistic.dice,
                lastPlaced = gameStatistic.lastPlaced,
                uiState = uiStates.getGhost()
            )

            BottomBoard(
                modifier = trayModifier,
                uiState = uiStates.getByType(DraggingType.BLOCK)
                    ?: throw IllegalStateException(),
                boardItem = gameStatistic.boardItem,
                boardState = boardState,
                diceItem = gameStatistic.dice,
                tray = gameStatistic.tray,
                pickedFace = luckyFace.takeIf { !spareActive && diceReady },
                onDieClick = { target ->
                    if (spareActive && gameStatistic.tray.spare?.value != null) {
                        onDiscardClick(target)
                    } else if (luckyFace != null && !spareActive) {
                        if (target == DiscardTarget.A) onLuckyDieClick(Slot.A)
                        else if (target == DiscardTarget.B) onLuckyDieClick(Slot.B)
                    }
                },
                onRollEnd = onRollEnd,
                onPlaceBlock = onPlaceBlock,
                onSpareRolled = onSpareRolled,
            )

            GameBottomPanel(
                modifier = panelModifier,
                bonusStatistic = gameStatistic.bonuses,
                freezeEnabled = !(gameStatistic.tray.slotA?.frozen == true
                        && gameStatistic.tray.slotB?.frozen == true),
                spareActive = spareActive,
                onBonusClick = {
                    when (it) {
                        BonusType.FREEZE -> if (!spareActive) onFreezeAll()
                        BonusType.ROTATE -> if (!spareActive && diceReady) onUseBonusClick(it)
                        BonusType.DRAFT -> if (!spareActive) onUseBonusClick(it)
                        BonusType.LUCKY -> if (!spareActive && diceReady) onLuckyClick()
                        else -> Unit
                    }
                },
                isLandscape = isLandscape,
                luckyEnabled = !spareActive,
                onBuyClick = onBuyBonusClick
            )
        }

        if (isLandscape) {
            Row(modifier = Modifier.weight(1f)) {
                field(Modifier.weight(0.46f).fillMaxHeight(), Modifier.weight(0.34f), Modifier.weight(0.2f))
            }
        } else {
            field(Modifier, Modifier.weight(1f), Modifier.height(120.dp))
        }
    }
}

@Composable
private fun Loading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlue)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            color = White
        )
    }
}

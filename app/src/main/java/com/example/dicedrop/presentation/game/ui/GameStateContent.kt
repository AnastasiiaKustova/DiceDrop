import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.SideEffect
import com.example.dicedrop.presentation.game.ui.header.HeaderButtons
import com.example.dicedrop.presentation.game.ui.header.SideCoins
import com.example.dicedrop.presentation.game.ui.header.SideScore
import androidx.compose.ui.unit.dp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import com.example.dicedrop.domain.entity.settings.GameSettings
import com.example.dicedrop.domain.feedback.GameFeedback
import com.example.dicedrop.presentation.common.LocalBackgroundMusic
import com.example.dicedrop.presentation.common.LocalGameFeedback
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
import com.example.dicedrop.presentation.game.ui.popups.StuckDialog
import com.example.dicedrop.presentation.game.ui.popups.StuckHint
import com.example.dicedrop.presentation.settings.ui.DrawPause
import com.example.dicedrop.presentation.ui.theme.DarkBlue
import com.example.dicedrop.presentation.ui.theme.White

// Сколько раз показывать окно «Ходов нет», прежде чем перейти на подсказку
private const val STUCK_DIALOG_LIMIT = 3

@Composable
fun GameStateContent(
    state: GameStore.State.GameState,
    clearedRows: List<Int>,
    clearedCols: List<Int>,
    comboPopup: ComboPopup?,
    trophyHighlighted: Boolean,
    rewardsToClaim: Int,
    settings: GameSettings,
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
    val feedback = LocalGameFeedback.current
    val music = LocalBackgroundMusic.current

    // Музыка молчит на паузе и в окнах конца игры и рекорда; если ушли с экрана с открытым окном - снова играет
    val musicPaused = showPause ||
            state is GameStore.State.GameState.Over ||
            state is GameStore.State.GameState.NewRecord
    DisposableEffect(musicPaused) {
        music.setPaused(musicPaused)
        onDispose { if (musicPaused) music.setPaused(false) }
    }

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

            // Блок никуда не встаёт, но бонус спасёт: первые разы - окно, потом только подсказка.
            // Закрытое окно больше не открывается, пока не сменится ход или кубики
            val rescueBonuses = if (state is GameStore.State.GameState.Loaded) game.rescueBonuses else emptyList()
            var stuckDialogClosed by rememberSaveable(game.moves, game.dice) { mutableStateOf(false) }
            val showStuckDialog = rescueBonuses.isNotEmpty() && !stuckDialogClosed &&
                    settings.stuckDialogsShown < STUCK_DIALOG_LIMIT &&
                    !showPause && !showLuckySheet && luckyFace == null
            fun closeStuckDialog() {
                stuckDialogClosed = true
                component.onStuckDialogClosed()
            }

            GameScreen(
                gameStatistic = game,
                clearedRows = clearedRows,
                clearedCols = clearedCols,
                comboPopup = comboPopup,
                isLandscape = isLandscape,
                trophyHighlighted = trophyHighlighted,
                rewardsToClaim = rewardsToClaim,
                luckyFace = luckyFace,
                rescueBonuses = rescueBonuses,
                showStuckHint = rescueBonuses.isNotEmpty() && !showStuckDialog,
                onShopClick = component::onShopClick,
                onAchievementsClick = component::onAchievementClick,
                onPauseClick = { showPause = true },
                onRollEnd = component::onRollEnd,
                onUseBonusClick = {
                    if (it == BonusType.ROTATE) {
                        feedback.play(GameFeedback.Sound.BONUS_ROTATE)
                        feedback.vibrate(GameFeedback.Haptic.BONUS)
                    }
                    component.onBonusClick(it)
                },
                onBuyBonusClick = component::onBuyBonusClick,
                onPlaceBlock = component::placeBlock,
                onFreezeAll = {
                    feedback.play(GameFeedback.Sound.BONUS_FREEZE)
                    feedback.vibrate(GameFeedback.Haptic.BONUS)
                    component.onFreezeAll()
                },
                onDiscardClick = {
                    feedback.play(GameFeedback.Sound.BONUS_DISCARD)
                    feedback.vibrate(GameFeedback.Haptic.BONUS)
                    component.onDiscardClick(it)
                },
                onSpareRolled = component::onSpareRolled,
                onLuckyClick = {
                    if (luckyFace != null) cancelLucky() else showLuckySheet = true
                },
                onLuckyDieClick = { slot ->
                    luckyFace?.let {
                        feedback.play(GameFeedback.Sound.BONUS_LUCKY)
                        feedback.vibrate(GameFeedback.Haptic.BONUS)
                        component.onLuckyFace(it, slot)
                    }
                    cancelLucky()
                }
            )

            if (showLuckySheet) {
                LaunchedEffect(Unit) {
                    feedback.play(GameFeedback.Sound.BONUS_LUCKY_SHEET)
                    feedback.vibrate(GameFeedback.Haptic.TICK)
                }
                LuckyChoiceDialog(
                    onPick = { face ->
                        luckyFace = face
                        showLuckySheet = false
                    },
                    onCancel = { cancelLucky() }
                )
            }

            if (showStuckDialog) {
                BackHandler { closeStuckDialog() }
                StuckDialog(
                    rescueBonuses = rescueBonuses,
                    onUseBonusClick = { closeStuckDialog() },
                    onRestartClick = {
                        closeStuckDialog()
                        component.restartGame()
                    }
                )
            }
        } else if (state == GameStore.State.GameState.Loading) {
            Loading()
        }

        when (state) {
            is GameStore.State.GameState.NewRecord -> {
                PlayOnce(key = "record${state.score}") {
                    feedback.play(GameFeedback.Sound.NEW_RECORD)
                    feedback.vibrate(GameFeedback.Haptic.CELEBRATE)
                }
                NewRecordDialog(
                    record = state.score,
                    previousRecord = state.prevState.gameStatistic.bestScore,
                    rewardsList = state.rewardsList,
                    confirmClick = { component.onConfirmNewRecord(state.rewardsList) }
                )
            }

            is GameStore.State.GameState.Over -> {
                PlayOnce(key = "over${state.score}") {
                    feedback.play(GameFeedback.Sound.GAME_OVER)
                    feedback.vibrate(GameFeedback.Haptic.GAME_OVER)
                }
                GameOverDialog(
                    gameScore = state.score,
                    bestScore = state.prevState.gameStatistic.bestScore,
                    canUndo = state.prevState.gameStatistic.reviveSnapshot != null,
                    onRestartClick = {
                        cancelLucky()
                        component.restartGame()
                    },
                    coinsEarned = state.prevState.gameStatistic.coinsEarned,
                    onDoubleCoinsClick = {
                        cancelLucky()
                        component.onDoubleCoinsClick()
                    },
                    onWatchAdsClick = component::onWatchAdsClick
                )
            }

            else -> Unit
        }

        if (showPause) {
            DrawPause(
                settings = settings,
                onSettingsChange = component::onSettingsChange,
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
    rescueBonuses: List<BonusType>,
    showStuckHint: Boolean,
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

    val uiStates = remember { GameUiState.getList() }
    val boardState = remember { BoardUiState() }

    // Поле, лоток и бонусы отдельно: в портрете они идут столбиком, на планшете - в разных колонках
    val field = @Composable { fieldModifier: Modifier ->
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
        }

    // Подсказка «используй бонус» - поверх лотка сверху, рядом с блоком, который не встаёт
    val trayContent = @Composable { trayModifier: Modifier ->
            Box(modifier = trayModifier) {
                BottomBoard(
                    modifier = Modifier.fillMaxSize(),
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
                if (showStuckHint) {
                    StuckHint(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 8.dp)
                    )
                }
            }
        }

    val panel = @Composable { panelModifier: Modifier ->
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
                highlighted = rescueBonuses,
                onBuyClick = onBuyBonusClick
            )
        }

    if (isLandscape) {
        // Планшет: слева монеты и счёт, по центру поле, справа кнопки, лоток и бонусы
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBlue)
        ) {
            val sideWidth = if (maxWidth < 1100.dp) 200.dp else 250.dp
            val trayMinWidth = 320.dp
            // Клетка такая, чтобы по высоте влезло поле 11×11, а по ширине - поле и блок 6×6 с линейкой
            val cell = minOf(
                (maxHeight - 90.dp) / 11,
                (maxWidth - sideWidth - trayMinWidth - 80.dp) / 11,
                (maxWidth - sideWidth - 200.dp) / 17
            ).coerceIn(24.dp, 46.dp)
            SideEffect { boardState.cellSize = cell }
            val trayWidth = maxOf(cell * 6 + 120.dp, trayMinWidth)

            Row(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .width(sideWidth)
                        .fillMaxHeight()
                        .padding(start = 24.dp, end = 24.dp, top = 28.dp, bottom = 30.dp)
                ) {
                    SideCoins(coins = gameStatistic.coins, onShopClick = onShopClick)
                    Spacer(modifier = Modifier.weight(1f))
                    SideScore(
                        totalScore = gameStatistic.totalScore,
                        bestScore = gameStatistic.bestScore,
                        combo = gameStatistic.combo
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    // Пустое место под высоту монет: так счёт оказывается ровно по центру экрана
                    Spacer(modifier = Modifier.height(52.dp))
                }
                field(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                Column(
                    modifier = Modifier
                        .width(trayWidth)
                        .fillMaxHeight()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 28.dp, end = 28.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        HeaderButtons(
                            trophyHighlighted = trophyHighlighted,
                            rewardsToClaim = rewardsToClaim,
                            onPauseClick = onPauseClick,
                            onAchievementsClick = onAchievementsClick,
                            buttonSize = 48.dp
                        )
                    }
                    trayContent(Modifier.weight(1f))
                    panel(Modifier)
                }
            }
        }
    } else {
        SideEffect { boardState.cellSize = BoardUiState.PortraitCell }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBlue),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
            field(Modifier)
            trayContent(Modifier.weight(1f))
            panel(Modifier.height(120.dp))
        }
    }
}

// Звук окна играет один раз: не повторяется, когда игрок вернулся из магазина или достижений.
// Состояние экрана в стеке навигации сохраняется, поэтому флаг переживает переход
@Composable
private fun PlayOnce(key: String, action: () -> Unit) {
    var played by rememberSaveable(key) { mutableStateOf(false) }
    LaunchedEffect(key) {
        if (!played) {
            played = true
            action()
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

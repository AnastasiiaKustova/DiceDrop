import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import com.example.dicedrop.domain.engine.BoardEngine.rescueChance
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
import com.example.dicedrop.presentation.ui.theme.LightBlue
import com.example.dicedrop.presentation.ui.theme.White

@Composable
fun GameStateContent(
    state: GameStore.State.GameState,
    clearedRows: List<Int>,
    clearedCols: List<Int>,
    comboPopup: ComboPopup?,
    component: GameComponent
) {
    var showLucky by remember { mutableStateOf(false) }
    var pickedFace by remember { mutableStateOf<Int?>(null) }
    var showPause by rememberSaveable { mutableStateOf(false) }

    // Системная "Назад" при открытой паузе закрывает паузу, а не выходит из игры
    BackHandler(enabled = showPause) {
        showPause = false
    }

    val configuration = LocalConfiguration.current

    val isLandscape = when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> true
        else -> false
    }

    Box(modifier = Modifier.fillMaxSize()) {

        when (state) {
            is GameStore.State.GameState.Loaded,
            is GameStore.State.GameState.NewRecord,
            is GameStore.State.GameState.Over -> {

                val gameState = when (state) {
                    is GameStore.State.GameState.Loaded -> state
                    is GameStore.State.GameState.NewRecord -> state.prevState
                    is GameStore.State.GameState.Over -> state.prevState
                    else -> error("Impossible")
                }

                GameScreen(
                    gameStatistic = gameState.gameStatistic,
                    clearedRows = clearedRows,
                    clearedCols = clearedCols,
                    comboPopup = comboPopup,
                    isLandscape = isLandscape,
                    onShopClick = component::onShopClick,
                    onPauseClick = { showPause = true },
                    onRollEnd = component::onRollEnd,
                    onUseBonusClick = component::onBonusClick,
                    onBuyBonusClick = component::onShopClick,
                    onPlaceBlock = component::placeBlock,
                    onFreezeAll = component::onFreezeAll,
                    onDiscardClick = component::onDiscardClick,
                    onSpareRolled = component::onSpareRolled,
                    pickedFace = pickedFace,
                    onPickedFace = { pickedFace = it },
                    onShowLucky = { showLucky = true },
                    onLuckyFace = component::onLuckyFace
                )
            }

            GameStore.State.GameState.Loading -> Loading()
            GameStore.State.GameState.Error,
            GameStore.State.GameState.Initial -> Unit
        }

        when (state) {
            is GameStore.State.GameState.NewRecord -> {
                NewRecordDialog(
                    record = state.score,
                    rewardsList = state.rewardsList,
                    isLandscape = isLandscape,
                    confirmClick = { component.onConfirmNewRecord(state.rewardsList) }
                )
            }

            is GameStore.State.GameState.Over -> {
                GameOverDialog(
                    gameScore = state.score,
                    rescueChance = state.prevState.gameStatistic.let { g ->
                        g.boardItem.rescueChance(g.bonuses.firstOrNull { it.type == BonusType.ROTATE }?.count ?: 0)
                    },
                    canUndo = state.prevState.gameStatistic.reviveSnapshot != null,
                    isLandscape = isLandscape,
                    onRestartClick = component::restartGame,
                    onWatchAdsClick = component::onWatchAdsClick
                )
            }

            else -> Unit
        }

        if (showLucky) {
            LuckyChoiceDialog(
                onPick = { face ->
                    pickedFace = face
                    showLucky = false
                },
            )
        }

        if (showPause) {
            DrawPause(
                onBackClick = { showPause = false },
                onRestartClick = {
                    showPause = false
                    component.restartGame()
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
    pickedFace: Int?,
    onPickedFace: (Int?) -> Unit,
    onShowLucky: () -> Unit,
    onShopClick: () -> Unit,
    onPauseClick: () -> Unit,
    onRollEnd: (result: DiceItem) -> Unit,
    onUseBonusClick: (BonusType) -> Unit,
    onBuyBonusClick: () -> Unit,
    onPlaceBlock: (r: Int, c: Int) -> Unit,
    onFreezeAll: () -> Unit,
    onDiscardClick: (DiscardTarget) -> Unit,
    onSpareRolled: (Int) -> Unit,
    onLuckyFace: (Int, Slot) -> Unit,
) {
    val spareActive = gameStatistic.tray.spare != null

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val uiStates = remember { GameUiState.getList() }
        val boardState = remember { BoardUiState() }

        GameHeader(
            modifier = Modifier.weight(0.12f),
            totalScore = gameStatistic.totalScore,
            bestScore = gameStatistic.bestScore,
            coins = gameStatistic.coins,
            combo = gameStatistic.combo,
            isLandscape = isLandscape,
            onPauseClick = onPauseClick,
            onShopClick = onShopClick
        )

        val content = @Composable{
            DrawPlayingField(
                modifier = Modifier.weight(0.46f),
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
                modifier = Modifier.weight(0.3f),
                uiState = uiStates.getByType(DraggingType.BLOCK)
                    ?: throw IllegalStateException(),
                boardItem = gameStatistic.boardItem,
                boardState = boardState,
                diceItem = gameStatistic.dice,
                isLandscape = isLandscape,
                tray = gameStatistic.tray,
                facePick = pickedFace != null,
                onDieClick = { target ->
                    if (spareActive && gameStatistic.tray.spare?.value != null) {
                        onDiscardClick(target)
                    } else if (pickedFace != null && !spareActive) {
                        if (target == DiscardTarget.A) onLuckyFace(pickedFace!!, Slot.A)
                        else if (target == DiscardTarget.B) onLuckyFace(pickedFace!!, Slot.B)
                        onPickedFace(null)
                    }
                },
                onRollEnd = onRollEnd,
                onPlaceBlock = onPlaceBlock,
                onSpareRolled = onSpareRolled,
            )

            GameBottomPanel(
                modifier = Modifier.weight(0.12f),
                bonusStatistic = gameStatistic.bonuses,
                freezeEnabled = !(gameStatistic.tray.slotA?.frozen == true
                        && gameStatistic.tray.slotB?.frozen == true),
                spareActive = spareActive,
                onBonusClick = {
                    when (it) {
                        BonusType.FREEZE -> if (!spareActive) onFreezeAll()
                        BonusType.ROTATE -> if (!spareActive && gameStatistic.dice != DiceItem.empty()) onUseBonusClick(it)
                        BonusType.DRAFT -> if (!spareActive) onUseBonusClick(it)
                        BonusType.LUCKY -> if (!spareActive) onShowLucky()
                        else -> Unit
                    }
                },
                isLandscape = isLandscape,
                luckyEnabled = !spareActive,
                onCounterClick = { onBuyBonusClick() }
            )
        }

        if (isLandscape) {
            Row(modifier = Modifier.weight(0.76f)) {
                content()
            }
        } else {
            content()
        }
    }
}

@Composable
private fun Loading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBlue)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            color = White
        )
    }
}
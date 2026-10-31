package com.example.dicedrop.trailer

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
import com.example.dicedrop.domain.entity.BoardItem
import com.example.dicedrop.domain.entity.BonusItem
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.domain.entity.DiceItem
import com.example.dicedrop.domain.entity.DraggingType
import com.example.dicedrop.domain.entity.TrayState
import com.example.dicedrop.domain.feedback.GameFeedback
import com.example.dicedrop.domain.feedback.GameFeedback.Companion.lineCleared
import com.example.dicedrop.presentation.common.DieFace
import com.example.dicedrop.presentation.common.LocalGameFeedback
import com.example.dicedrop.presentation.game.ui.AchievementBanner
import com.example.dicedrop.presentation.game.ui.AchievementBannerCard
import com.example.dicedrop.presentation.game.ui.board.BottomBoard
import com.example.dicedrop.presentation.game.ui.board.ComboPopup
import com.example.dicedrop.presentation.game.ui.board.DrawPlayingField
import com.example.dicedrop.presentation.game.ui.board.animation.AnimatedDices
import com.example.dicedrop.presentation.game.ui.board.components.GameCell
import com.example.dicedrop.presentation.game.ui.board.components.Grid
import com.example.dicedrop.presentation.game.ui.bottompanel.BonusButton
import com.example.dicedrop.presentation.game.ui.bottompanel.getIconResId
import com.example.dicedrop.presentation.game.ui.bottompanel.getPrice
import com.example.dicedrop.presentation.game.ui.bottompanel.getTitleResId
import com.example.dicedrop.presentation.game.ui.drag.BoardUiState
import com.example.dicedrop.presentation.game.ui.drag.GameUiState
import com.example.dicedrop.presentation.game.ui.header.GameHeader
import com.example.dicedrop.presentation.game.ui.popups.NewRecordDialog
import com.example.dicedrop.presentation.game.ui.popups.StuckHint
import com.example.dicedrop.presentation.game.ui.popups.onTrayEdge
import com.example.dicedrop.presentation.ui.theme.CoinEdge
import com.example.dicedrop.presentation.ui.theme.CoinGold
import com.example.dicedrop.presentation.ui.theme.CoinShade
import com.example.dicedrop.presentation.ui.theme.DarkBlue
import com.example.dicedrop.presentation.ui.theme.DeepBlue
import com.example.dicedrop.presentation.ui.theme.DieBorder
import com.example.dicedrop.presentation.ui.theme.DieShade
import com.example.dicedrop.presentation.ui.theme.DieWhite
import com.example.dicedrop.presentation.ui.theme.Navy
import com.example.dicedrop.presentation.ui.theme.Orange
import com.example.dicedrop.presentation.ui.theme.White
import com.example.dicedrop.presentation.ui.theme.Yellow
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Постановочное поле: сверху три пустых ряда, ниже плита с прорезью посередине и выемка внизу.
// Блок 2×5 из первого броска закрывает два нижних ряда (комбо ×2). Потом выпадает 6×6, которому
// места нет; «Счастливая грань» делает из него 6×1, он встаёт в прорезь и закрывает шесть рядов
// разом (комбо ×8) - поле очищается полностью
private val StartBoard = listOf(
    "...........",
    "...........",
    "...........",
    "#####.#####",
    "#####.#####",
    "#####.#####",
    "#####.#####",
    "#####.#####",
    "#####.#####",
    "###.....###",
    "###.....###",
)

// Счёт: 9 000 + 400 + 4 800 = 14 200 - выше рекорда 12 300
private const val START_SCORE = 9000
private const val BEST_SCORE = 12300
private const val START_COINS = 340

// Порядок бонусов в панели, как в игре
private val PanelOrder = listOf(BonusType.ROTATE, BonusType.FREEZE, BonusType.LUCKY, BonusType.DRAFT)

// Кубик на линейке в лотке (GameBottomBoard): 38dp, линейка 43dp, зазор до блока 8dp,
// линейка не короче кубика с засечками
private val TrayDie = 38.dp
private const val RULER_HALF = 21.5f
private const val RULER_GAP = 8f
private const val RULER_MIN = 70f
private val BigDie = 150.dp
private val LogoDie = 100.dp
// Надписи - под шапкой с зазором, поверх пустых верхних рядов поля: ниже верхних ~10% кадра,
// которые RuStore может обрезать
private val CaptionGap = 14.dp
// Кубики броска в лотке игры (AnimatedDices): 46dp, между центрами 72dp
private val RollDie = 46.dp
private const val ROLL_HALF_GAP = 36f

private fun boardOf(rows: List<String>) = BoardItem(board = rows.map { row -> BooleanArray(row.length) { row[it] == '#' } })

// Осколки клеток при очистке линий: летят в стороны и падают
private class Shard(val x: Float, val y: Float, val vx: Float, val vy: Float, val size: Float, val spin: Float, val color: Color)
private class Burst(val shards: List<Shard>) {
    val time = Animatable(0f)
}

// Монета, которая летит с поля в счётчик шапки
private class FlyingCoin(val from: Offset, val to: Offset) {
    val progress = Animatable(0f)
}

private const val SHARD_LIFE = 0.75f

// Игра по сценарию: всё состояние экрана в одном месте, сценарий меняет его по времени
private class TrailerState {
    var board by mutableStateOf(boardOf(StartBoard))
    var dice by mutableStateOf(DiceItem.empty())
    var score by mutableIntStateOf(START_SCORE)
    val shownScore = Animatable(START_SCORE.toFloat())
    var coins by mutableIntStateOf(START_COINS)
    var combo by mutableIntStateOf(0)
    var lastPlaced by mutableStateOf<List<Int>>(emptyList())
    var clearedRows by mutableStateOf<List<Int>>(emptyList())
    var clearedCols by mutableStateOf<List<Int>>(emptyList())
    var comboPopup by mutableStateOf<ComboPopup?>(null)
    var bonuses by mutableStateOf(PanelOrder.map { BonusItem(it, 3) })

    // Бросок в лотке: id перезапускает анимацию кубиков, values - что выпадет
    var rollId by mutableIntStateOf(0)
    var rollValues by mutableStateOf<DiceItem?>(null)
    // Лоток спрятан, пока в него падают кубики заставки
    val trayShown = Animatable(0f)

    // Бонусы появляются, только когда блок не встаёт
    val panel = Animatable(0f)
    val panelButtons = List(4) { Animatable(0f) }
    var stuck by mutableStateOf(false)

    // Надпись над полем на подложке
    var caption by mutableStateOf<String?>(null)
    val captionLetters = Animatable(0f)
    val captionAlpha = Animatable(1f)

    var banner by mutableStateOf<AchievementBanner?>(null)
    val bannerShown = Animatable(0f)
    // Второе достижение приходит снизу, первое поднимается выше
    var banner2 by mutableStateOf<AchievementBanner?>(null)
    val banner2Shown = Animatable(0f)
    val bannerLift = Animatable(0f)
    var record by mutableStateOf(false)

    // Эффекты: осколки клеток, монеты в шапку
    val bursts = mutableStateListOf<Burst>()
    val flyingCoins = mutableStateListOf<FlyingCoin>()

    // Карусель бонусов: pos - какая карточка в центре, у каждой карточки своя анимация примера
    val carouselShown = Animatable(0f)
    val carouselPos = Animatable(0f)
    val carouselAnims = List(4) { Animatable(0f) }
    var carouselCenter by mutableStateOf(Offset.Zero)

    // «Счастливый» сработал: кубик на линейке кувыркается с 6 на 1
    var flipAt by mutableStateOf<Offset?>(null)
    val flip = Animatable(0f)

    // Большие кубики: заставка (падают в середину лотка, потом расходятся на линейки)
    // и финал (падают в логотип)
    val cover = Animatable(1f)
    var introSpots by mutableStateOf<List<Offset>>(emptyList())
    var introTargets by mutableStateOf<List<Offset>>(emptyList())
    val introFall = Animatable(0f)
    val introHop = Animatable(0f)
    val introLand = Animatable(1f)
    val introGlide = Animatable(0f)
    var introDone by mutableStateOf(false)
    val logoFall = Animatable(0f)
    val logoHop = Animatable(0f)
    val logoLand = Animatable(1f)
    val logoSwing = Animatable(0f)
    val logoTitle = Animatable(0f)
    val logoShine = Animatable(0f)

    // Палец, в пикселях экрана; press - нажатие
    var finger by mutableStateOf<Offset?>(null)
    val fingerPress = Animatable(0f)

    var headerBottom by mutableStateOf(0f)
    var captionBottom by mutableStateOf(0f)
    var hintTop by mutableStateOf(0f)
    var trayCenter by mutableStateOf(Offset.Zero)
    var panelRect by mutableStateOf(Rect.Zero)
    var screen by mutableStateOf(Rect.Zero)

    var finished by mutableStateOf(false)
}

@Composable
fun TrailerContent() {
    var run by remember { mutableIntStateOf(0) }
    // Каждый запуск - с чистого состояния
    key(run) {
        val state = remember { TrailerState() }
        val uiState = remember { GameUiState(DraggingType.BLOCK) }
        // Клетка чуть меньше, чем в игре: с ней блок 6×6 целиком помещается в лоток вместе с панелью бонусов
        val boardState = remember { BoardUiState().apply { cellSize = 25.dp } }
        var started by remember { mutableStateOf(false) }

        TrailerScreen(state, uiState, boardState)

        if (!started) {
            // До касания - пустой синий экран: успеваешь включить запись
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { started = true }
            )
        } else {
            val feedback = LocalGameFeedback.current
            val density = LocalDensity.current.density
            val context = LocalContext.current
            // Музыка и сценарий стартуют вместе; сценарий ждёт музыку в ключевых местах
            val music = remember { MediaPlayer.create(context, R.raw.music_trailer) }
            DisposableEffect(Unit) {
                music.start()
                onDispose { music.release() }
            }
            LaunchedEffect(Unit) {
                Script(state, uiState, boardState, density, feedback) { music.currentPosition }.play()
            }
            // После конца ролика касание запускает его заново
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { if (state.finished) run++ }
            )
        }
    }
}

@Composable
private fun TrailerScreen(state: TrailerState, uiState: GameUiState, boardState: BoardUiState) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { state.screen = it.boundsInRoot() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBlue),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopBar(state)
            DrawPlayingField(
                boardItem = state.board,
                boardState = boardState,
                clearedRows = state.clearedRows,
                clearedCols = state.clearedCols,
                comboPopup = state.comboPopup,
                diceItem = state.dice,
                lastPlaced = state.lastPlaced,
                uiState = uiState.takeIf { it.ghostRow != null && it.ghostCol != null }
            )
            Tray(state, uiState, boardState, Modifier.weight(1f))
            BonusPanel(state)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                // Подложка того же цвета, что шапка, - между ними зазор, чтобы не сливались
                .offset { IntOffset(0, (state.headerBottom + CaptionGap.toPx()).roundToInt()) }
                .onGloballyPositioned { state.captionBottom = it.boundsInRoot().bottom },
            contentAlignment = Alignment.TopCenter
        ) {
            Caption(state)
        }
        BonusCarousel(state)
        LuckyFlip(state)
        Shards(state)
        FlyingCoins(state)
        AchievementBannerOverlay(state)

        if (state.record) {
            NewRecordDialog(
                record = state.score,
                previousRecord = BEST_SCORE,
                rewardsList = listOf(BonusItem(BonusType.COIN, 65), BonusItem(BonusType.LUCKY, 1)),
                confirmClick = {},
                buttonText = "Начать игру"
            )
        }

        Cover(state)
        IntroDice(state)
        FinalLogo(state, constraints.maxHeight.toFloat())

        state.finger?.let { Finger(it, state.fingerPress.value) }
    }
}

// Шапка игры весь ролик, как в игре: счёт бежит вверх на каждой очистке, монеты прилетают в счётчик
@Composable
private fun TopBar(state: TrailerState) {
    GameHeader(
        modifier = Modifier.onGloballyPositioned { state.headerBottom = it.boundsInRoot().bottom },
        totalScore = state.shownScore.value.roundToInt(),
        bestScore = BEST_SCORE,
        coins = state.coins,
        combo = state.combo,
        trophyHighlighted = state.banner != null,
        rewardsToClaim = if (state.record) 2 else listOfNotNull(state.banner, state.banner2).size,
        onPauseClick = {},
        onShopClick = {},
        onAchievementsClick = {}
    )
}

@Composable
private fun Tray(state: TrailerState, uiState: GameUiState, boardState: BoardUiState, modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .onGloballyPositioned {
                state.trayCenter = it.boundsInRoot().center
            }
    ) {
        val rollValues = state.rollValues
        if (state.dice == DiceItem.empty()) {
            // Бросок как в лотке игры, но с заранее известным результатом. Пустой лоток
            // рисуем сами: BottomBoard без кубиков сразу начал бы случайный бросок
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DarkBlue)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                if (rollValues != null) {
                    key(state.rollId) {
                        AnimatedDices(
                            rolling = true,
                            // Левый кубик в лотке - ширина блока, правый - высота
                            values = DiceItem(dice1 = rollValues.dice2, dice2 = rollValues.dice1),
                            onRollEnd = { state.dice = rollValues }
                        )
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = state.trayShown.value }
            ) {
                BottomBoard(
                    uiState = uiState,
                    boardItem = state.board,
                    boardState = boardState,
                    diceItem = state.dice,
                    tray = TrayState(),
                    pickedFace = null,
                    onDieClick = {},
                    onRollEnd = {},
                    onPlaceBlock = { _, _ -> },
                    onSpareRolled = {}
                )
            }
        }

        // Подсказка - над лотком, как в игре, чтобы не закрывать кубики
        if (state.stuck) {
            StuckHint(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .onTrayEdge()
                    .onGloballyPositioned { state.hintTop = it.boundsInRoot().top }
            )
        }
    }
}

// Панель бонусов выезжает снизу, когда блок не встаёт, кнопки выскакивают по очереди
@Composable
private fun BonusPanel(state: TrailerState) {
    val p = state.panel.value
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp * p)
            .clipToBounds()
            .onGloballyPositioned { state.panelRect = it.boundsInRoot() },
        contentAlignment = Alignment.TopCenter
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(120.dp)
                .background(DeepBlue)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            state.bonuses.forEachIndexed { i, bonus ->
                val pop = state.panelButtons[i].value
                Box(
                    modifier = Modifier
                        .weight(1f)
                        // Кнопка вылетает снизу с наклоном и садится на место с пружинкой
                        .graphicsLayer {
                            scaleX = pop
                            scaleY = pop
                            alpha = pop.coerceIn(0f, 1f)
                            translationY = (1f - pop) * 70.dp.toPx()
                            rotationZ = (1f - pop) * (if (i % 2 == 0) -25f else 25f)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    BonusButton(
                        iconResId = bonus.type.getIconResId(),
                        count = bonus.count,
                        price = bonus.type.getPrice(),
                        highlighted = state.stuck && bonus.type == BonusType.LUCKY,
                        onBonusClick = {},
                        onBuyClick = {}
                    )
                }
            }
        }
    }
}

private const val LETTER_STAGGER = 35f
private const val LETTER_POP = 260f

private fun captionMillis(text: String) = (text.length * LETTER_STAGGER + LETTER_POP).toInt()

// Пружинка «с перелётом»: буква вырастает чуть больше и садится на место
private fun backOut(x: Float): Float {
    val c = 2.2f
    val u = x - 1f
    return 1f + (c + 1f) * u * u * u + c * u * u
}

// Крупная надпись сценария: буквы выпрыгивают по одной. Длинная - мельче или в две строки,
// чтобы по краям кадра оставался запас
@Composable
private fun Caption(state: TrailerState) {
    val text = state.caption ?: return
    val lines = text.split('\n')
    val longest = lines.maxOf { it.length }
    val fontSize = when {
        longest > 16 -> 23.sp
        longest > 14 -> 26.sp
        else -> 30.sp
    }
    val shown = state.captionLetters.value * (text.length * LETTER_STAGGER + LETTER_POP)
    val textStyle = TextStyle(
        shadow = Shadow(color = Color.Black.copy(alpha = 0.35f), offset = Offset(0f, 4f), blurRadius = 8f)
    )
    // Подложка выскакивает первой, буквы - за ней
    val plate = backOut((state.captionLetters.value * 5f).coerceAtMost(1f))
    Column(
        modifier = Modifier
            .graphicsLayer {
                alpha = state.captionAlpha.value * (state.captionLetters.value * 8f).coerceAtMost(1f)
                scaleX = 0.85f + 0.15f * plate
                scaleY = 0.85f + 0.15f * plate
            }
            .background(DarkBlue.copy(alpha = 0.9f), RoundedCornerShape(22.dp))
            .padding(horizontal = 22.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var index = 0
        lines.forEach { line ->
            Row {
                line.forEach { ch ->
                    val local = ((shown - index * LETTER_STAGGER) / LETTER_POP).coerceIn(0f, 1f)
                    index++
                    Text(
                        modifier = Modifier.graphicsLayer {
                            val s = backOut(local)
                            scaleX = s
                            scaleY = s
                            alpha = (local * 3f).coerceAtMost(1f)
                            translationY = (1f - local) * 14.dp.toPx()
                        },
                        text = ch.toString(),
                        color = White,
                        fontSize = fontSize,
                        lineHeight = fontSize,
                        fontWeight = FontWeight.Black,
                        style = textStyle
                    )
                }
            }
            index++
        }
    }
}

// Бонусы каруселью: карточки проезжают над полем справа налево, в центре - крупно. Каждая
// показывает простой пример своего бонуса, не связанный с полем
private val CarouselOrder = listOf(BonusType.ROTATE, BonusType.FREEZE, BonusType.DRAFT, BonusType.LUCKY)
private val CardWidth = 210.dp
private val CardHeight = 160.dp
private val CardStep = 170.dp
private val CardShape = RoundedCornerShape(26.dp)

@Composable
private fun BonusCarousel(state: TrailerState) {
    val shown = state.carouselShown.value
    if (shown <= 0f) return
    val pos = state.carouselPos.value
    val center = state.carouselCenter
    CarouselOrder.forEachIndexed { i, type ->
        val d = i - pos
        if (abs(d) > 1.6f) return@forEachIndexed
        Column(
            modifier = Modifier
                .offset {
                    IntOffset(
                        (center.x - CardWidth.toPx() / 2).roundToInt(),
                        (center.y - CardHeight.toPx() / 2).roundToInt()
                    )
                }
                .size(CardWidth, CardHeight)
                .graphicsLayer {
                    translationX = d * CardStep.toPx()
                    val focus = 1f - abs(d).coerceAtMost(1f)
                    val s = (0.72f + 0.28f * focus) * (0.6f + 0.4f * shown)
                    scaleX = s
                    scaleY = s
                    alpha = shown * (0.35f + 0.65f * focus)
                }
                .background(DarkBlue, CardShape)
                .border(3.dp, if (abs(d) < 0.5f) Yellow else White.copy(alpha = 0.3f), CardShape)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(type.getIconResId()),
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .background(White, CircleShape)
                        .padding(5.dp)
                )
                Text(
                    text = stringResource(type.getTitleResId()),
                    color = White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                BonusExample(type, state.carouselAnims[i].value)
            }
        }
    }
}

// Пример бонуса в карточке, p - ход анимации 0..1
@Composable
private fun BonusExample(type: BonusType, p: Float) {
    when (type) {
        // Блок поворачивается на 90°
        BonusType.ROTATE -> Box(modifier = Modifier.graphicsLayer { rotationZ = 90f * p }) {
            Grid(2, 4) { _, _ -> GameCell(Yellow, 18.dp) }
        }

        // Кубик покрывается льдом
        BonusType.FREEZE -> Box(contentAlignment = Alignment.Center) {
            DieFace(value = 4, size = 58.dp)
            Image(
                painter = painterResource(R.drawable.ic_ice_shell),
                contentDescription = null,
                modifier = Modifier
                    .requiredSize(72.dp)
                    .graphicsLayer {
                        alpha = 0.72f * p
                        val s = 1.4f - 0.4f * p
                        scaleX = s
                        scaleY = s
                    }
            )
        }

        // К двум кубикам добавляется третий, лишний гаснет
        BonusType.DRAFT -> Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val pop = (p / 0.5f).coerceIn(0f, 1f)
            val fade = ((p - 0.55f) / 0.45f).coerceIn(0f, 1f)
            DieFace(
                value = 2,
                size = 44.dp,
                modifier = Modifier.graphicsLayer {
                    alpha = 1f - 0.75f * fade
                    scaleX = 1f - 0.2f * fade
                    scaleY = 1f - 0.2f * fade
                }
            )
            DieFace(value = 5, size = 44.dp)
            DieFace(
                value = 6,
                size = 44.dp,
                modifier = Modifier.graphicsLayer {
                    val s = backOut(pop)
                    scaleX = s
                    scaleY = s
                    alpha = pop
                }
            )
        }

        // Грань кубика переворачивается с 6 на 1
        else -> {
            val faces = remember { faceSequence(2, 1) }
            CubeDie(size = 58.dp, phase = 2f * p, faces = faces)
        }
    }
}

// «Счастливый» сработал: поверх кубика ширины на линейке он же кувыркается с 6 на 1 и чуть подпрыгивает
@Composable
private fun LuckyFlip(state: TrailerState) {
    val at = state.flipAt ?: return
    val p = state.flip.value
    val density = LocalDensity.current
    val half = with(density) { (TrayDie / 2).roundToPx() }
    val faces = remember { faceSequence(2, 1) }
    CubeDie(
        size = TrayDie,
        phase = 2f * p,
        faces = faces,
        modifier = Modifier
            .offset { IntOffset(at.x.roundToInt() - half, at.y.roundToInt() - half) }
            .graphicsLayer {
                val s = 1f + 0.35f * sin(PI.toFloat() * p)
                scaleX = s
                scaleY = s
            }
    )
}

// Осколки очищенных клеток: разлетаются вверх и в стороны, падают, вращаются и тают
@Composable
private fun Shards(state: TrailerState) {
    if (state.bursts.isEmpty()) return
    Canvas(modifier = Modifier.fillMaxSize()) {
        val gravity = 1600.dp.toPx()
        state.bursts.forEach { burst ->
            val t = burst.time.value * SHARD_LIFE
            val fade = (1f - t / SHARD_LIFE).coerceIn(0f, 1f)
            burst.shards.forEach { sh ->
                val x = sh.x + sh.vx * t
                val y = sh.y + sh.vy * t + gravity * t * t / 2
                rotate(sh.spin * t, pivot = Offset(x, y)) {
                    drawRoundRect(
                        color = sh.color.copy(alpha = fade),
                        topLeft = Offset(x - sh.size / 2, y - sh.size / 2),
                        size = Size(sh.size, sh.size),
                        cornerRadius = CornerRadius(sh.size * 0.25f)
                    )
                }
            }
        }
    }
}

// Монеты с поля летят дугой в счётчик шапки
@Composable
private fun FlyingCoins(state: TrailerState) {
    val density = LocalDensity.current
    val size = 26.dp
    val half = with(density) { (size / 2).roundToPx() }
    state.flyingCoins.forEach { coin ->
        val p = coin.progress.value
        if (p <= 0f || p >= 1f) return@forEach
        // Дуга: сначала вверх-вбок, потом к счётчику
        val control = Offset(coin.from.x + with(density) { 90.dp.toPx() }, coin.from.y - with(density) { 120.dp.toPx() })
        val u = 1f - p
        val at = coin.from * (u * u) + control * (2 * u * p) + coin.to * (p * p)
        Image(
            painter = painterResource(R.drawable.coin_star),
            contentDescription = null,
            modifier = Modifier
                .offset { IntOffset(at.x.roundToInt() - half, at.y.roundToInt() - half) }
                .size(size)
                .graphicsLayer {
                    val s = 1.2f - 0.4f * p
                    scaleX = s
                    scaleY = s
                }
        )
    }
}

// Плашки достижений: в ролике выезжают снизу посередине лотка - после очистки там пусто.
// Вторая приходит снизу на место первой и сдвигает её выше, пара стоит по центру лотка
@Composable
private fun AchievementBannerOverlay(state: TrailerState) {
    val first = state.banner ?: return
    var cardHeight by remember { mutableIntStateOf(0) }
    val step = with(LocalDensity.current) { cardHeight + 10.dp.toPx() } / 2f
    // Одна - стоит ниже, на месте будущей второй; с приходом второй поднимается на ступень
    BannerOnTray(state, first, state.bannerShown.value, shift = step - 2 * step * state.bannerLift.value) { cardHeight = it }
    state.banner2?.let { BannerOnTray(state, it, state.banner2Shown.value, shift = step) {} }
}

@Composable
private fun BannerOnTray(
    state: TrailerState,
    banner: AchievementBanner,
    shown: Float,
    shift: Float,
    onHeight: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                onHeight(placeable.height)
                layout(placeable.width, placeable.height) {
                    placeable.place(0, (state.trayCenter.y + shift - placeable.height / 2f).roundToInt())
                }
            }
            .graphicsLayer {
                alpha = shown.coerceIn(0f, 1f)
                translationY = (1f - shown) * 160.dp.toPx()
            }
    ) {
        AchievementBannerCard(banner)
    }
}

// Палец: полупрозрачный круг с белой обводкой, как в обучении; при нажатии сжимается
@Composable
private fun Finger(at: Offset, press: Float) {
    val density = LocalDensity.current
    val size = 46.dp
    val half = with(density) { (size / 2).roundToPx() }
    Box(
        modifier = Modifier
            .offset { IntOffset(at.x.roundToInt() - half, at.y.roundToInt() - half) }
            .size(size)
            .graphicsLayer {
                scaleX = 1f - 0.25f * press
                scaleY = 1f - 0.25f * press
            }
            .background(White.copy(alpha = 0.45f + 0.25f * press), CircleShape)
            .border(3.dp, White, CircleShape)
    )
}

@Composable
private fun Cover(state: TrailerState) {
    val cover = state.cover.value
    if (cover <= 0f) return
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = cover }
            .background(DarkBlue)
    )
}

// Кубик, который по-настоящему кувыркается вперёд через ребро, вокруг одной горизонтальной оси:
// верхняя грань уходит вниз, сверху выходит следующая. phase - сколько четвертей оборота сделано,
// faces - какая грань смотрит на нас после k четвертей
@Composable
private fun CubeDie(
    size: Dp,
    phase: Float,
    faces: (Int) -> Int,
    gold: Boolean = false,
    modifier: Modifier = Modifier
) {
    val k = floor(phase).toInt()
    val a = (phase - k) * PI.toFloat() / 2
    Canvas(modifier = modifier.size(size)) {
        drawCube(front = faces(k), next = faces(k + 1), a = a, gold = gold)
    }
}

// Кубик одним телом: общая обводка на обе видимые грани, грани касаются друг друга по ребру.
// Сверху - грань, которая выходит (высота s·sin a), под ней - уходящая (s·cos a). В покое рисуется
// так же, как кубик игры (drawDie), поэтому подмена на настоящий кубик в лотке незаметна
private fun DrawScope.drawCube(front: Int, next: Int, a: Float, gold: Boolean) {
    val s = size.minDimension
    val c = cos(a)
    val sn = sin(a)
    val radius = s * 0.25f
    val edge = if (gold) CoinEdge else DieBorder
    val top = s / 2 - s / 2 * (c + sn)
    drawRoundRect(
        color = edge,
        topLeft = Offset(0f, top),
        size = Size(s, s * (c + sn)),
        cornerRadius = CornerRadius(radius)
    )
    drawCubeSide(next, top, sn, gold)
    drawCubeSide(front, top + s * sn, c, gold)
}

// Одна грань в проекции: всё, что по высоте, сжато в visible раз; отвёрнутая грань темнее
private fun DrawScope.drawCubeSide(value: Int, y: Float, visible: Float, gold: Boolean) {
    if (visible < 0.02f) return
    val s = size.minDimension
    val radius = s * 0.25f
    val border = (s * 0.06f).coerceAtLeast(2.dp.toPx())
    val face = if (gold) CoinGold else DieWhite
    val shade = if (gold) CoinShade else DieShade
    val inner = Size(s - border * 2, (s - border * 2) * visible)
    val corner = CornerRadius(radius - border, (radius - border) * visible)
    val topLeft = Offset(border, y + border * visible)
    drawRoundRect(color = shade, topLeft = topLeft, size = inner, cornerRadius = corner)
    drawRoundRect(
        color = face,
        topLeft = topLeft,
        size = Size(inner.width, inner.height - s * 0.1f * visible),
        cornerRadius = corner
    )

    val pip = s * 0.185f
    val near = s * 0.18f + pip / 2
    val far = s - near
    val mid = s / 2
    val lift = s * 0.045f
    val points = when (value) {
        1 -> listOf(mid to mid)
        2 -> listOf(near to near, far to far)
        3 -> listOf(near to near, mid to mid, far to far)
        4 -> listOf(near to near, far to near, near to far, far to far)
        5 -> listOf(near to near, far to near, mid to mid, near to far, far to far)
        else -> listOf(near to near, far to near, near to mid, far to mid, near to far, far to far)
    }
    points.forEach { (px, py) ->
        val cy = y + (py - lift) * visible
        drawOval(
            color = Navy,
            topLeft = Offset(px - pip / 2, cy - pip / 2 * visible),
            size = Size(pip, pip * visible)
        )
    }
    if (visible < 1f) {
        drawRoundRect(
            color = Color.Black.copy(alpha = 0.3f * (1f - visible)),
            topLeft = topLeft,
            size = inner,
            cornerRadius = corner
        )
    }
}

// Грани при кувырке вокруг одной оси, как у настоящего кубика: по кругу четыре грани, через одну -
// противоположные (сумма 7). После end четвертей сверху last
private fun faceSequence(end: Int, last: Int): (Int) -> Int {
    val side = if (last == 1 || last == 6) 2 else 1
    val ring = listOf(last, side, 7 - last, 7 - side)
    return { k -> if (k >= end) last else ring[((end - k) % 4 + 4) % 4] }
}

private const val INTRO_TURNS = 6
private const val LOGO_TURNS = 5

// Заставка: два больших кубика падают сверху в середину лотка, кувыркаясь и уменьшаясь до кубиков
// броска; последнюю четверть оборота докатываются после удара, с подскоком. Потом расходятся
// на линейки - становятся кубиками первого броска (5 - ширина, 2 - высота)
@Composable
private fun IntroDice(state: TrailerState) {
    val spots = state.introSpots
    val targets = state.introTargets
    if (state.introDone || spots.isEmpty() || targets.isEmpty()) return
    val fall = state.introFall.value
    val hop = state.introHop.value
    val land = state.introLand.value
    val glide = state.introGlide.value
    val density = LocalDensity.current.density
    val half = BigDie.value * density / 2
    val faces = remember { listOf(faceSequence(INTRO_TURNS + 1, 5), faceSequence(INTRO_TURNS + 1, 2)) }

    spots.forEachIndexed { i, spot ->
        val side = if (i == 0) 1f else -1f
        val target = targets[i]
        // Падают почти отвесно; пока большие - врозь, чтобы не налезать друг на друга, и сходятся
        // по мере уменьшения
        // Старт целиком за верхним краем, с запасом на наклон: до падения кубиков в кадре не видно
        val startY = -half - 40f * density
        val x = spot.x + side * 48f * density * (1f - fall) + (target.x - spot.x) * glide
        // Падение с ускорением, подскок после удара
        val y = startY + (spot.y - startY) * fall * fall - sin(PI.toFloat() * hop) * 16f * density +
                (target.y - spot.y) * glide
        CubeDie(
            size = BigDie,
            phase = INTRO_TURNS * fall + hop,
            faces = faces[i],
            modifier = Modifier
                .offset { IntOffset((x - half).roundToInt(), (y - half).roundToInt()) }
                .graphicsLayer {
                    val s = (1f - (1f - RollDie.value / BigDie.value) * fall) *
                            (1f - (1f - TrayDie.value / RollDie.value) * glide)
                    scaleX = s
                    scaleY = s
                    rotationZ = side * 10f * (1f - fall)
                }
                // Удар о стол: кубик сплющивается и выпрямляется
                .graphicsLayer {
                    scaleX = 1.2f - 0.2f * land
                    scaleY = 0.8f + 0.2f * land
                    transformOrigin = TransformOrigin(0.5f, 1f)
                }
        )
    }
}

// Финал: по «Начать игру» экран синеет, сверху падают кувыркающиеся кубики, ударяются, подскакивают,
// встают гранями 5 и золотой 3 и покачиваются. Под ними проявляется название, по нему пробегает блик
@Composable
private fun FinalLogo(state: TrailerState, screenHeight: Float) {
    val fall = state.logoFall.value
    if (fall <= 0f) return
    val hop = state.logoHop.value
    val land = state.logoLand.value
    val swing = state.logoSwing.value
    val title = state.logoTitle.value
    val shine = state.logoShine.value
    val faces = remember { listOf(faceSequence(LOGO_TURNS + 1, 5), faceSequence(LOGO_TURNS + 1, 3)) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                listOf(-10f, 8f).forEachIndexed { i, angle ->
                    CubeDie(
                        size = LogoDie,
                        phase = LOGO_TURNS * fall + hop,
                        faces = faces[i],
                        gold = i == 1,
                        modifier = Modifier
                            .padding(top = if (i == 1) 14.dp else 0.dp)
                            .graphicsLayer {
                                translationY = -(1f - fall * fall) * screenHeight * 0.65f -
                                        sin(PI.toFloat() * hop) * 22.dp.toPx()
                                // До удара кубики ровные, после - качнутся в наклон логотипа с перелётом
                                rotationZ = angle * swing
                            }
                            .graphicsLayer {
                                scaleX = 1.16f - 0.16f * land
                                scaleY = 0.84f + 0.16f * land
                                transformOrigin = TransformOrigin(0.5f, 1f)
                            }
                    )
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                modifier = Modifier
                    .graphicsLayer {
                        alpha = title
                        translationY = (1f - title) * 18.dp.toPx()
                        val s = 0.92f + 0.08f * title
                        scaleX = s
                        scaleY = s
                        compositingStrategy = CompositingStrategy.Offscreen
                    }
                    .drawWithContent {
                        drawContent()
                        // Блик: светлая косая полоса проходит по буквам слева направо
                        if (shine > 0f && shine < 1f) {
                            val band = size.width * 0.25f
                            val x = -band + (size.width + 2 * band) * shine
                            drawRect(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color.Transparent, White.copy(alpha = 0.75f), Color.Transparent),
                                    start = Offset(x - band, 0f),
                                    end = Offset(x + band, size.height * 0.6f)
                                ),
                                blendMode = BlendMode.SrcAtop
                            )
                        }
                    },
                text = buildAnnotatedString {
                    append("DICE ")
                    withStyle(SpanStyle(color = Yellow)) { append("DROP") }
                },
                color = White,
                // Около 2/3 ширины экрана: по краям кадра остаётся запас
                fontSize = 46.sp,
                lineHeight = 46.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        }
    }
}

// Сценарий ролика. Время - паузами между шагами, в ключевых местах сценарий ждёт музыку
private class Script(
    private val s: TrailerState,
    private val ui: GameUiState,
    private val board: BoardUiState,
    private val density: Float,
    private val feedback: GameFeedback,
    private val musicMs: () -> Int
) {
    private fun dp(value: Float) = value * density
    private val random = Random(20261029)

    // Отметки времени в logcat (тег Trailer) - по ним подгоняется музыка
    private val t0 = System.currentTimeMillis()
    private fun mark(name: String) = Log.d("Trailer", "$name ${System.currentTimeMillis() - t0} music ${musicMs()}")

    private suspend fun waitMusic(ms: Int) {
        while (musicMs() < ms) delay(5)
    }

    // Сценарий целиком: в нём живут фоновые анимации ходов (счёт, монеты), чтобы ход их не ждал
    private lateinit var scope: CoroutineScope

    suspend fun play() = coroutineScope {
        scope = this
        // Заставка: блок первого броска уже в лотке, но спрятан - по нему видно, куда разойдутся кубики.
        // Падают они в середину лотка, как при броске в игре: справа ширина, слева высота
        mark("intro")
        s.dice = DiceItem(dice1 = 2, dice2 = 5)
        delay(60)
        s.introTargets = rulerDice()
        s.introSpots = listOf(
            s.trayCenter + Offset(dp(ROLL_HALF_GAP), 0f),
            s.trayCenter - Offset(dp(ROLL_HALF_GAP), 0f)
        )
        // Кубики падают и ударяются о лоток ровно на сильную долю музыки
        waitMusic(LAND_HIT_MS - INTRO_FALL_MS)
        mark("fall")
        launch {
            delay(INTRO_FALL_MS - 550L)
            // В звуке броска удар - через 550 мс от начала
            feedback.play(GameFeedback.Sound.DICE_ROLL)
        }
        launch {
            delay(INTRO_FALL_MS - 800L)
            s.cover.animateTo(0f, tween(800, easing = LinearEasing))
        }
        s.introFall.animateTo(1f, tween(INTRO_FALL_MS, easing = LinearEasing))
        mark("land")
        feedback.vibrate(GameFeedback.Haptic.TICK)
        s.introLand.snapTo(0f)
        launch { s.introLand.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)) }
        s.introHop.animateTo(1f, tween(340, easing = FastOutSlowInEasing))
        // Кубики расходятся на линейки - под ними проявляется блок 2×5
        s.introGlide.animateTo(1f, tween(320, easing = FastOutSlowInEasing))
        s.trayShown.animateTo(1f, tween(180))
        s.introDone = true

        // Ставь блоки - и сразу собираются две линии
        mark("place")
        showCaption("СТАВЬ БЛОКИ")
        delay(200)
        drag(row = 9, col = 3, onPlaced = { changeCaption("ОЧИЩАЙ ПОЛЕ") })
        delay(1000)

        // Выпадает 6×6: его пробуют поставить - места нет, он возвращается
        mark("stuck")
        launch { changeCaption("НЕКУДА ПОСТАВИТЬ\nБЛОК?") }
        roll(DiceItem(dice1 = 6, dice2 = 6))
        delay(300)
        tryDrag(row = 3, col = 2)
        delay(100)

        // Используй бонусы: выезжает панель, над полем каруселью проезжают все бонусы, последней -
        // «Счастливая грань»; она улетает в свою кнопку, кнопка пульсирует
        mark("bonuses")
        launch { changeCaption("ИСПОЛЬЗУЙ БОНУСЫ") }
        s.stuck = true
        launch {
            s.panel.animateTo(1f, tween(380, easing = FastOutSlowInEasing))
            s.panelButtons.forEach { button ->
                feedback.play(GameFeedback.Sound.BUTTON_CLICK)
                launch { button.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMediumLow)) }
                delay(120)
            }
        }
        // Карусель - посередине между надписью и подсказкой на стыке поля и лотка, обе меряются
        // на экране. Ждём, пока сменится надпись, чтобы мерить уже новую
        delay(230)
        s.carouselCenter = Offset(s.screen.center.x, (s.captionBottom + s.hintTop) / 2)
        // Каждая карточка въезжает в центр и сама показывает пример своего бонуса
        s.carouselShown.animateTo(1f, tween(120, easing = FastOutSlowInEasing))
        CarouselOrder.indices.forEach { i ->
            if (i > 0) {
                launch { s.carouselPos.animateTo(i.toFloat(), tween(220, easing = FastOutSlowInEasing)) }
                delay(110)
            }
            s.carouselAnims[i].animateTo(1f, tween(380, easing = FastOutSlowInEasing))
            delay(20)
        }
        launch { s.carouselShown.animateTo(0f, tween(200)) }
        // Палец жмёт «Счастливый» на панели - кубик ширины кувыркается с 6 на 1, блок становится 6×1
        tapAt(luckyButton())
        s.stuck = false
        val die = rulerDice()[0]
        s.flipAt = die
        s.flip.animateTo(1f, tween(380, easing = FastOutSlowInEasing))
        s.dice = DiceItem(dice1 = 6, dice2 = 1)
        s.flipAt = null
        s.bonuses = s.bonuses.map { if (it.type == BonusType.LUCKY) it.copy(count = it.count - 1) else it }
        feedback.play(GameFeedback.Sound.BONUS_LUCKY)
        launch { shatter(listOf(die), perPoint = 14, colors = listOf(Yellow, Yellow, White)) }
        delay(60)
        // Поле очищается на сильную долю музыки: палец подлетает и тащит блок ~2 с
        mark("wait clear")
        waitMusic(CLEAR_HIT_MS - 2040)
        mark("bigdrag")
        // Счёт в шапке обгонит рекорд на очистке
        launch { hideCaption() }
        // Плашка достижения выезжает прямо на очистке, с ней - надпись
        drag(row = 3, col = 5, onPlaced = {
            delay(150)
            launch { showCaption("ПОЛУЧАЙ ДОСТИЖЕНИЯ") }
            showAchievement()
        })

        // Сразу за плашкой - новый рекорд, «Начать игру» - и финал
        mark("record")
        launch { hideCaption() }
        s.record = true
        feedback.play(GameFeedback.Sound.NEW_RECORD)
        delay(1200)
        // Кубики логотипа ударяются ровно на удар в музыке: нажатие ~670 мс, падение LOGO_FALL_MS
        waitMusic(LOGO_HIT_MS - LOGO_FALL_MS - 670)
        mark("start game")
        tapAt(continueButton())
        feedback.play(GameFeedback.Sound.BUTTON_CLICK)
        s.finger = null
        launch { s.cover.animateTo(1f, tween(350)) }
        s.logoFall.animateTo(1f, tween(LOGO_FALL_MS, easing = LinearEasing))
        s.record = false
        mark("logo")
        feedback.vibrate(GameFeedback.Haptic.CELEBRATE)
        s.logoLand.snapTo(0f)
        launch { s.logoLand.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)) }
        launch {
            s.logoSwing.animateTo(1f, spring(dampingRatio = 0.35f, stiffness = Spring.StiffnessLow))
        }
        s.logoHop.animateTo(1f, tween(380, easing = FastOutSlowInEasing))
        s.logoTitle.animateTo(1f, tween(650, easing = FastOutSlowInEasing))
        s.logoShine.animateTo(1f, tween(900, easing = FastOutSlowInEasing))
        delay(1000)
        mark("end")
        s.finished = true
    }

    // Плашка достижения
    // Два достижения подряд: «Большой взрыв» за шесть линий, за ним «Чистый лист» за пустое поле
    private suspend fun showAchievement() = coroutineScope {
        mark("achievement")
        s.banner = AchievementBanner(
            titleId = R.string.ach_lines_at_once_6_title,
            descriptionId = R.string.ach_lines_at_once_6_desc,
            reward = 75
        )
        feedback.play(GameFeedback.Sound.ACHIEVEMENT)
        launch { s.bannerShown.animateTo(1f, spring(dampingRatio = Spring.DampingRatioLowBouncy)) }
        delay(1100)
        s.banner2 = AchievementBanner(
            titleId = R.string.ach_board_clear_1_title,
            descriptionId = R.string.ach_board_clear_1_desc,
            reward = 20
        )
        feedback.play(GameFeedback.Sound.ACHIEVEMENT)
        launch { s.bannerLift.animateTo(1f, spring(dampingRatio = Spring.DampingRatioLowBouncy)) }
        s.banner2Shown.animateTo(1f, spring(dampingRatio = Spring.DampingRatioLowBouncy))
        delay(1300)
        launch { s.bannerShown.animateTo(0f, tween(300)) }
        s.banner2Shown.animateTo(0f, tween(300))
        s.banner = null
        s.banner2 = null
    }

    private suspend fun showCaption(text: String) {
        s.caption = text
        s.captionAlpha.snapTo(1f)
        s.captionLetters.snapTo(0f)
        s.captionLetters.animateTo(1f, tween(captionMillis(text), easing = LinearEasing))
    }

    private suspend fun hideCaption() {
        s.captionAlpha.animateTo(0f, tween(220))
        s.caption = null
    }

    private suspend fun changeCaption(text: String) {
        hideCaption()
        showCaption(text)
    }

    // Бросок длится, пока кубики не приземлятся
    private suspend fun roll(values: DiceItem) {
        s.rollValues = values
        s.rollId++
        while (s.dice != values) delay(16)
    }

    // Центры кубиков на линейках лотка: [ширина над блоком, высота слева от блока]. Блок стоит
    // по центру своей линейки, если та длиннее него
    private fun rulerDice(): List<Offset> {
        val cell = board.cellSize.value * density
        val blockW = s.dice.dice2 * cell
        val blockH = s.dice.dice1 * cell
        val padX = (maxOf(blockW, dp(RULER_MIN)) - blockW) / 2
        val padY = (maxOf(blockH, dp(RULER_MIN)) - blockH) / 2
        return listOf(
            Offset(ui.elementStartX + blockW / 2, ui.elementStartY - padY - dp(RULER_GAP + RULER_HALF)),
            Offset(ui.elementStartX - padX - dp(RULER_GAP + RULER_HALF), ui.elementStartY + blockH / 2)
        )
    }

    // «Счастливая грань» - третья кнопка из четырёх в панели (отступы панели по 12dp)
    private fun luckyButton(): Offset {
        val inner = s.panelRect.width - dp(24f)
        return Offset(s.panelRect.left + dp(12f) + inner * 2.5f / 4f, s.panelRect.center.y)
    }

    // «Начать игру» в окне рекорда (там же, где «Продолжить» в игре)
    private fun continueButton() = Offset(s.screen.center.x, s.screen.center.y + dp(CONTINUE_FROM_CENTER))

    // Палец подлетает к точке и нажимает
    private suspend fun tapAt(target: Offset) {
        val from = s.finger ?: Offset(target.x + dp(60f), target.y + dp(90f))
        val move = Animatable(0f)
        move.animateTo(1f, tween(380, easing = FastOutSlowInEasing)) {
            s.finger = from + (target - from) * value
        }
        s.fingerPress.animateTo(1f, tween(90))
        s.fingerPress.animateTo(0f, tween(140))
    }

    // Перенос блока на (x, y) пикселей от лотка: блок идёт лёгкой дугой,
    // на поле - подсветка места, как при настоящем перетаскивании
    private suspend fun moveBlock(toX: Float, toY: Float, fromX: Float, fromY: Float, ms: Int, grip: Offset) {
        val cellPx = board.cellSize.value * density
        val startX = grip.x - s.dice.dice2 * cellPx / 2
        val startY = grip.y - s.dice.dice1 * cellPx / 2
        val move = Animatable(0f)
        move.animateTo(1f, tween(ms, easing = FastOutSlowInEasing)) {
            val arc = sin(PI.toFloat() * value) * dp(-24f)
            val x = fromX + (toX - fromX) * value + arc
            val y = fromY + (toY - fromY) * value
            ui.dragOffsetX = (x / density).dp
            ui.dragOffsetY = (y / density).dp
            s.finger = Offset(grip.x + x, grip.y + y)
            ui.ghostRow = ((startY + y - board.boardOffsetY) / cellPx).roundToInt()
            ui.ghostCol = ((startX + x - board.boardOffsetX) / cellPx).roundToInt()
        }
    }

    // Палец тащит блок из лотка на клетку (row, col), блок встаёт, полные линии очищаются
    private suspend fun drag(row: Int, col: Int, onPlaced: suspend () -> Unit = {}) = coroutineScope {
        val cellPx = board.cellSize.value * density
        val startX = ui.elementStartX
        val startY = ui.elementStartY
        val grip = Offset(startX + s.dice.dice2 * cellPx / 2, startY + s.dice.dice1 * cellPx / 2)

        // Палец ложится на блок: подлетает, если уже на экране, иначе появляется на месте
        if (s.finger != null) tapAt(grip) else s.finger = grip
        delay(200)
        ui.dragging = true
        moveBlock(
            toX = board.boardOffsetX + col * cellPx - startX,
            toY = board.boardOffsetY + row * cellPx - startY,
            fromX = 0f, fromY = 0f, ms = 750, grip = grip
        )
        delay(250)
        s.finger = null
        launch { onPlaced() }
        place(row, col)
    }

    // Блок пробуют поставить, но места нет: подсветка не загорается, блок возвращается в лоток
    private suspend fun tryDrag(row: Int, col: Int) {
        val cellPx = board.cellSize.value * density
        val startX = ui.elementStartX
        val startY = ui.elementStartY
        val grip = Offset(startX + s.dice.dice2 * cellPx / 2, startY + s.dice.dice1 * cellPx / 2)
        val toX = board.boardOffsetX + col * cellPx - startX
        val toY = board.boardOffsetY + row * cellPx - startY

        s.finger = grip
        delay(200)
        ui.dragging = true
        moveBlock(toX = toX, toY = toY, fromX = 0f, fromY = 0f, ms = 650, grip = grip)
        delay(250)
        s.finger = null
        feedback.vibrate(GameFeedback.Haptic.REJECT)
        moveBlock(toX = 0f, toY = 0f, fromX = toX, fromY = toY, ms = 300, grip = grip)
        ui.resetState()
    }

    private suspend fun place(row: Int, col: Int) {
        val h = s.dice.dice1
        val w = s.dice.dice2
        val cells = s.board.board.map { it.copyOf() }
        val placed = mutableListOf<Int>()
        for (r in row until row + h) for (c in col until col + w) {
            cells[r][c] = true
            placed += r * 11 + c
        }
        ui.resetState()
        s.dice = DiceItem.empty()
        s.rollValues = null
        s.lastPlaced = placed
        s.board = s.board.copy(board = cells)
        feedback.vibrate(GameFeedback.Haptic.PLACE)

        val rows = (0 until 11).filter { r -> cells[r].all { it } }
        val cols = (0 until 11).filter { c -> cells.all { it[c] } }
        val count = rows.size + cols.size
        if (count == 0) {
            s.combo = 0
            return
        }
        val combo = s.combo + count
        val points = count * 100 * combo
        val cleared = cells.map { it.copyOf() }
        rows.forEach { r -> cleared[r].fill(false) }
        cols.forEach { c -> cleared.forEach { it[c] = false } }
        // Поле очищено целиком - сверху монеты за очистку, как в игре
        val coins = count * 3 + if (cleared.all { r -> r.none { it } }) 15 else 0

        delay(120)
        mark("clear")
        s.board = s.board.copy(board = cleared)
        s.clearedRows = rows
        s.clearedCols = cols
        s.combo = combo
        s.score += points
        s.comboPopup = ComboPopup(combo = combo, points = points, coins = coins)
        feedback.lineCleared(combo)
        // Счёт в шапке бежит вверх, монеты с поля летят в счётчик
        scope.launch { s.shownScore.animateTo(s.score.toFloat(), tween(1500, easing = FastOutSlowInEasing)) }
        scope.launch { flyCoins(coins) }
        coroutineScope {
            launch { burst(rows, cols) }
            delay(400)
            s.clearedRows = emptyList()
            s.clearedCols = emptyList()
        }
    }

    // Осколки из каждой клетки очищенных линий
    private suspend fun burst(rows: List<Int>, cols: List<Int>) {
        val cellPx = board.cellSize.value * density
        val cells = rows.flatMap { r -> (0 until 11).map { c -> r to c } } +
                cols.flatMap { c -> (0 until 11).map { r -> r to c } }
        val centers = cells.distinct().map { (r, c) ->
            Offset(board.boardOffsetX + (c + 0.5f) * cellPx, board.boardOffsetY + (r + 0.5f) * cellPx)
        }
        shatter(centers, perPoint = 3, colors = listOf(Yellow, Yellow, Orange, White))
    }

    // Осколки или искры из каждой точки: разлетаются, падают и тают
    private suspend fun shatter(centers: List<Offset>, perPoint: Int, colors: List<Color>) {
        val shards = centers.flatMap { center ->
            List(perPoint) {
                val angle = random.nextFloat() * 2f * PI.toFloat()
                val speed = dp(120f + random.nextFloat() * 260f)
                Shard(
                    x = center.x, y = center.y,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed - dp(320f),
                    size = dp(5f + random.nextFloat() * 6f),
                    spin = (random.nextFloat() - 0.5f) * 1440f,
                    color = colors[random.nextInt(colors.size)]
                )
            }
        }
        val b = Burst(shards)
        s.bursts += b
        b.time.animateTo(1f, tween((SHARD_LIFE * 1000).toInt(), easing = LinearEasing))
        s.bursts -= b
    }

    // Монеты летят из середины поля в счётчик шапки, счётчик прибавляет по прилёту каждой
    private suspend fun flyCoins(total: Int) = coroutineScope {
        if (total <= 0) return@coroutineScope
        delay(250)
        val from = Offset(s.screen.center.x, board.boardOffsetY + 5.5f * board.cellSize.value * density)
        val to = Offset(dp(COIN_X), dp(COIN_Y))
        val n = 8
        repeat(n) { i ->
            launch {
                delay(i * 70L)
                val coin = FlyingCoin(from + Offset(dp((i - n / 2) * 10f), 0f), to)
                s.flyingCoins += coin
                coin.progress.animateTo(1f, tween(650, easing = FastOutSlowInEasing))
                s.flyingCoins -= coin
                s.coins += total / n + if (i < total % n) 1 else 0
                feedback.play(GameFeedback.Sound.COIN)
            }
        }
    }

    companion object {
        // Удары в музыке (DiceDrop sounds/trailer_music.js): кубики легли, очистка поля, логотип
        const val LAND_HIT_MS = 2235
        const val CLEAR_HIT_MS = 15_784
        const val LOGO_HIT_MS = 23_526
        const val INTRO_FALL_MS = 1900
        const val LOGO_FALL_MS = 1100

        // Положение кнопки окна рекорда, снятое со скриншота на телефоне 1080×2340 (в dp)
        const val CONTINUE_FROM_CENTER = 228f

        // Монета в счётчике шапки: слева, по центру полосы 92dp
        const val COIN_X = 30f
        const val COIN_Y = 46f
    }
}

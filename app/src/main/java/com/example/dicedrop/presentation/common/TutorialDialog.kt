package com.example.dicedrop.presentation.common

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
import com.example.dicedrop.domain.catalog.EconomyCatalog
import com.example.dicedrop.domain.catalog.ShopItemCatalog
import com.example.dicedrop.domain.feedback.GameFeedback
import com.example.dicedrop.presentation.game.ui.board.comboColor
import com.example.dicedrop.presentation.ui.theme.CellBG
import com.example.dicedrop.presentation.ui.theme.DarkBlue
import com.example.dicedrop.presentation.ui.theme.DeepBlue
import com.example.dicedrop.presentation.ui.theme.MidBlue
import com.example.dicedrop.presentation.ui.theme.White
import com.example.dicedrop.presentation.ui.theme.Yellow

private const val TUTORIAL_STEPS = 9

private val tutorialTitles = listOf(
    R.string.tutorial_title_roll,
    R.string.tutorial_title_place,
    R.string.tutorial_title_lines,
    R.string.tutorial_title_combo,
    R.string.tutorial_title_bonuses,
    R.string.rotate,
    R.string.freeze,
    R.string.draft,
    R.string.lucky
)

private val tutorialTexts = listOf(
    R.string.tutorial_text_roll,
    R.string.tutorial_text_place,
    R.string.tutorial_text_lines,
    R.string.tutorial_text_combo,
    R.string.tutorial_text_bonuses,
    R.string.tutorial_text_rotate,
    R.string.tutorial_text_freeze,
    R.string.tutorial_text_draft,
    R.string.tutorial_text_lucky
)

// Иконка рядом с заголовком - только на шагах про конкретный бонус
private val tutorialIcons = mapOf(
    6 to R.drawable.bonus_swap_c_flat,
    7 to R.drawable.bonus_freeze_a_flat,
    8 to R.drawable.bonus_roll3_a_flat,
    9 to R.drawable.bonus_lucky_b_flat
)

private val PassedDot = Color(0xFFB9CDEF)
private val Ice = Color(0x80A0D7FF)
private val IceEdge = Color(0xFFA8DCFF)

// Окно «Как играть»: шаги с зацикленной анимацией, точки прогресса, «Назад»/«Далее».
// Открывается при первом запуске и по кнопке в паузе и настройках
@Composable
fun TutorialDialog(
    onClose: () -> Unit,
    onFinish: () -> Unit = onClose
) {
    var step by rememberSaveable { mutableIntStateOf(1) }
    val feedback = LocalGameFeedback.current
    BackHandler(onBack = onClose)

    DialogScrim {
        Column(
            modifier = Modifier
                .padding(horizontal = 22.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            // «Пропустить» отдельно над окном, чтобы не путать с кнопками шагов
            Row(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .height(36.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(DarkBlue)
                    .clickable {
                        feedback.play(GameFeedback.Sound.BUTTON_CLICK)
                        onClose()
                    }
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = stringResource(R.string.tutorial_skip),
                    color = White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Icon(
                    imageVector = GameIcons.Skip,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(12.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkBlue, RoundedCornerShape(30.dp))
                    .consumeClicks()
                    .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.alpha(0.85f),
                    text = stringResource(R.string.how_to_play).uppercase(),
                    color = White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )

                Box(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth()
                        .height(232.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(DeepBlue),
                    contentAlignment = Alignment.Center
                ) {
                    // При смене шага анимация начинается сначала
                    key(step) {
                        when (step) {
                            1 -> RollScene()
                            2 -> PlaceScene()
                            3 -> LinesScene()
                            4 -> ComboScene()
                            5 -> BonusesScene()
                            6 -> RotateScene()
                            7 -> FreezeScene()
                            8 -> DraftScene()
                            else -> LuckyScene()
                        }
                    }
                }

                Row(
                    modifier = Modifier.padding(top = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tutorialIcons[step]?.let { icon ->
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(icon),
                                contentDescription = null,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                    Text(
                        text = stringResource(tutorialTitles[step - 1]),
                        color = White,
                        fontSize = 24.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // Строки выравниваются по длине, чтобы не оставалось одного слова на строке
                Text(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .fillMaxWidth()
                        .alpha(0.85f),
                    text = stringResource(tutorialTexts[step - 1]),
                    color = White,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    minLines = 3,
                    style = LocalTextStyle.current.copy(lineBreak = LineBreak.Heading)
                )

                Row(modifier = Modifier.padding(top = 8.dp)) {
                    for (n in 1..TUTORIAL_STEPS) {
                        val width by animateDpAsState(
                            targetValue = if (n == step) 20.dp else 8.dp,
                            label = "tutorialDot"
                        )
                        val label = stringResource(R.string.tutorial_step, n, TUTORIAL_STEPS)
                        Box(
                            modifier = Modifier
                                .size(width = 24.dp, height = 28.dp)
                                .clickable { step = n }
                                .semantics { contentDescription = label },
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(width = width, height = 8.dp)
                                    .background(
                                        color = when {
                                            n == step -> Yellow
                                            n < step -> PassedDot
                                            else -> CellBG
                                        },
                                        shape = RoundedCornerShape(4.dp)
                                    )
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GameButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.tutorial_back),
                        onClick = { step-- },
                        color = MidBlue,
                        contentColor = White,
                        fontSize = 18.sp,
                        enabled = step > 1,
                        icon = GameIcons.Back,
                        iconSize = 18.dp
                    )
                    GameButton(
                        modifier = Modifier.weight(1.4f),
                        text = stringResource(
                            if (step < TUTORIAL_STEPS) R.string.tutorial_next else R.string.tutorial_play
                        ),
                        onClick = { if (step < TUTORIAL_STEPS) step++ else onFinish() },
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}

// Время сцены от 0 до 1, крутится по кругу
@Composable
private fun sceneTime(durationMillis: Int): Float {
    val time by rememberInfiniteTransition(label = "tutorialScene").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "tutorialTime"
    )
    return time
}

// Значение по ключевым кадрам (доля времени to значение), между кадрами - плавный переход
private fun track(t: Float, vararg frames: Pair<Float, Float>): Float {
    if (t <= frames.first().first) return frames.first().second
    for (i in 1 until frames.size) {
        val (t1, v1) = frames[i]
        if (t <= t1) {
            val (t0, v0) = frames[i - 1]
            return v0 + (v1 - v0) * FastOutSlowInEasing.transform((t - t0) / (t1 - t0))
        }
    }
    return frames.last().second
}

// То же время, но со сдвигом - для элементов, которые появляются друг за другом
private fun shifted(t: Float, by: Float): Float = (t - by + 1f) % 1f

@Composable
private fun Cells(rows: Int, cols: Int, cell: Dp, color: (row: Int, col: Int) -> Color) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        repeat(rows) { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                repeat(cols) { col ->
                    Box(
                        modifier = Modifier
                            .size(cell)
                            .background(color(row, col), RoundedCornerShape(5.dp))
                    )
                }
            }
        }
    }
}

// Мини-поле: '#' - занятая клетка. Клетка 24dp + зазор 3dp, поэтому позиция клетки - 27dp * индекс
@Composable
private fun MiniBoard(
    rows: List<String>,
    cellColor: (row: Int, filled: Boolean) -> Color = { _, filled -> if (filled) Yellow else CellBG },
    overlay: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .background(DarkBlue, RoundedCornerShape(14.dp))
            .padding(8.dp)
    ) {
        Cells(rows.size, rows.first().length, 24.dp) { row, col -> cellColor(row, rows[row][col] == '#') }
        overlay()
    }
}

// Палец, который тащит блок
@Composable
private fun TapDot(modifier: Modifier, alpha: Float, scale: Float) {
    Box(
        modifier = modifier
            .size(32.dp)
            .graphicsLayer {
                this.alpha = alpha
                scaleX = scale
                scaleY = scale
            }
            .background(White.copy(alpha = 0.55f), CircleShape)
            .border(3.dp, White, CircleShape)
    )
}

// Рамка больше кубика, но не раздвигает раскладку: иначе кубик с ней встаёт ниже соседних
@Composable
private fun BoxScope.AroundDie(size: Dp, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.matchParentSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.requiredSize(size)) {
            content()
        }
    }
}

// Кольцо-нажатие вокруг кубика; at - доля сцены, когда нажимают
@Composable
private fun BoxScope.TapRing(t: Float, at: Float = 0.46f) {
    val scale = track(t, 0f to 0.6f, at to 0.6f, at + 0.06f to 1f, at + 0.18f to 1.5f)
    AroundDie(size = 68.dp) {
        Box(
            modifier = Modifier
                .requiredSize(68.dp)
                .graphicsLayer {
                    alpha = track(t, 0f to 0f, at to 0f, at + 0.06f to 1f, at + 0.18f to 0f)
                    scaleX = scale
                    scaleY = scale
                }
                .border(3.dp, White, RoundedCornerShape(18.dp))
        )
    }
}

// Появление результата; at - доля сцены, когда он выскакивает. В конце круга исчезает,
// чтобы следующий круг начался с чистого экрана
@Composable
private fun LatePop(t: Float, at: Float, content: @Composable () -> Unit) {
    val scale = track(t, 0f to 0.3f, at to 0.3f, at + 0.1f to 1.1f, at + 0.16f to 1f)
    Box(
        modifier = Modifier.graphicsLayer {
            alpha = track(t, 0f to 0f, at to 0f, at + 0.1f to 1f, 0.92f to 1f, 1f to 0f)
            scaleX = scale
            scaleY = scale
        }
    ) {
        content()
    }
}

@Composable
private fun RollScene() {
    val t = sceneTime(2600)
    val blockScale = track(t, 0f to 0.3f, 0.3f to 0.3f, 0.42f to 1.1f, 0.5f to 1f)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            RollingDie(value = 2, t = t)
            RollingDie(value = 3, t = shifted(t, 0.05f))
        }
        Icon(
            imageVector = GameIcons.ArrowRight,
            contentDescription = null,
            tint = White,
            modifier = Modifier.size(26.dp)
        )
        Box(
            modifier = Modifier.graphicsLayer {
                alpha = track(t, 0f to 0f, 0.3f to 0f, 0.42f to 1f)
                scaleX = blockScale
                scaleY = blockScale
            }
        ) {
            Cells(2, 3, 22.dp) { _, _ -> Yellow }
        }
    }
}

@Composable
private fun RollingDie(value: Int, t: Float) {
    DieFace(
        value = value,
        size = 56.dp,
        modifier = Modifier.graphicsLayer {
            rotationZ = track(t, 0f to 0f, 0.1f to -22f, 0.2f to 16f, 0.28f to -6f, 0.34f to 0f)
            translationY = track(t, 0f to 0f, 0.1f to -16f, 0.2f to 0f).dp.toPx()
        }
    )
}

@Composable
private fun PlaceScene() {
    val t = sceneTime(3200)
    MiniBoard(rows = listOf("##.....", "##...##", "#....##", "#...###", "#...###")) {
        Box(
            modifier = Modifier
                .offset(x = 27.dp, y = 81.dp)
                .graphicsLayer {
                    translationX = track(t, 0f to 70f, 0.12f to 70f, 0.55f to 0f).dp.toPx()
                    translationY = track(t, 0f to 130f, 0.12f to 130f, 0.55f to 0f).dp.toPx()
                    alpha = track(t, 0f to 1f, 0.9f to 1f, 1f to 0f)
                }
        ) {
            Cells(2, 3, 24.dp) { _, _ -> Yellow }
            TapDot(
                modifier = Modifier.offset(x = 22.dp, y = 12.dp),
                alpha = track(t, 0f to 1f, 0.6f to 1f, 0.66f to 0f),
                scale = track(t, 0f to 1f, 0.55f to 1f, 0.6f to 0.8f)
            )
        }
    }
}

@Composable
private fun LinesScene() {
    val t = sceneTime(3600)
    // Собранный ряд вспыхивает и становится пустым: жёлтый -> белый -> цвет пустой клетки
    val flash = track(t, 0f to 0f, 0.5f to 0f, 0.56f to 1f, 0.64f to 1f, 0.78f to 2f)
    val clearColor = if (flash <= 1f) lerp(Yellow, White, flash) else lerp(White, CellBG, flash - 1f)

    MiniBoard(
        rows = listOf(".......", "##.....", "##...#.", "###..##", "##...##"),
        cellColor = { row, filled ->
            when {
                !filled -> CellBG
                row == 4 -> clearColor
                else -> Yellow
            }
        }
    ) {
        Box(
            modifier = Modifier
                .offset(x = 54.dp, y = 108.dp)
                .graphicsLayer {
                    translationX = track(t, 0f to 60f, 0.1f to 60f, 0.45f to 0f).dp.toPx()
                    translationY = track(t, 0f to 100f, 0.1f to 100f, 0.45f to 0f).dp.toPx()
                    alpha = track(t, 0f to 1f, 0.94f to 1f, 1f to 0f)
                }
        ) {
            Cells(1, 3, 24.dp) { _, _ -> clearColor }
            TapDot(
                modifier = Modifier.offset(x = 22.dp, y = (-4).dp),
                alpha = track(t, 0f to 1f, 0.5f to 1f, 0.55f to 0f),
                scale = track(t, 0f to 1f, 0.45f to 1f, 0.5f to 0.8f)
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 32.dp)
                .graphicsLayer {
                    alpha = track(t, 0f to 0f, 0.56f to 0f, 0.64f to 1f, 0.86f to 1f, 1f to 0f)
                    translationY = track(t, 0f to 12f, 0.56f to 12f, 0.64f to 0f, 0.86f to 0f, 1f to -10f).dp.toPx()
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "+100",
                color = White,
                fontSize = 26.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Black
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.coin_star),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "+${EconomyCatalog.COINS_PER_LINE}",
                    color = Yellow,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
private fun ComboScene() {
    val t = sceneTime(3600)
    val pulse = track((t * 3f) % 1f, 0f to 1f, 0.5f to 1.14f, 1f to 1f)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = 1250.spaced(),
                color = White,
                fontSize = 40.sp,
                lineHeight = 42.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .graphicsLayer {
                        scaleX = pulse
                        scaleY = pulse
                    }
                    .background(comboColor(3), RoundedCornerShape(8.dp))
                    .padding(horizontal = 9.dp),
                text = stringResource(R.string.combo_badge, 3),
                color = White,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Black
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf(100, 200, 300).forEachIndexed { i, points ->
                val ti = shifted(t, i * 0.14f)
                Column(
                    modifier = Modifier
                        .width(76.dp)
                        .graphicsLayer {
                            alpha = track(ti, 0f to 0f, 0.12f to 1f, 0.85f to 1f, 1f to 0f)
                            translationY = track(ti, 0f to 10f, 0.12f to 0f).dp.toPx()
                        }
                        .background(DarkBlue, RoundedCornerShape(14.dp))
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier.alpha(0.85f),
                        text = stringResource(R.string.tutorial_move, i + 1),
                        color = White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "+$points",
                        color = if (i == 2) Yellow else White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

@Composable
private fun BonusesScene() {
    // Иконки появляются по очереди один раз и остаются, без повтора
    val appear = remember { Animatable(0f) }
    LaunchedEffect(Unit) { appear.animateTo(1f, tween(1400, easing = LinearEasing)) }
    val t = appear.value
    val minPrice = ShopItemCatalog.all.filter { it.bonusType != null }.minOf { it.price }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            tutorialIcons.values.forEachIndexed { i, icon ->
                val start = i * 0.2f
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            alpha = track(t, 0f to 0f, start to 0f, start + 0.25f to 1f)
                            translationY = track(t, 0f to 10f, start to 10f, start + 0.25f to 0f).dp.toPx()
                        }
                        .size(58.dp)
                        .background(White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(icon),
                        contentDescription = null,
                        modifier = Modifier.size(42.dp)
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .background(DarkBlue, RoundedCornerShape(14.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.coin_star),
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = stringResource(R.string.tutorial_price_from, minPrice),
                color = White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
private fun RotateScene() {
    val t = sceneTime(3600)
    val swap = track(t, 0f to 0f, 0.04f to 0f, 0.2f to 1f, 0.8f to 1f, 0.96f to 0f)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DieFace(
                value = 2,
                size = 56.dp,
                modifier = Modifier.graphicsLayer { translationX = (64f * swap).dp.toPx() }
            )
            DieFace(
                value = 3,
                size = 56.dp,
                modifier = Modifier.graphicsLayer { translationX = (-64f * swap).dp.toPx() }
            )
        }
        Icon(
            imageVector = GameIcons.ArrowRight,
            contentDescription = null,
            tint = White,
            modifier = Modifier.size(22.dp)
        )
        Box(
            modifier = Modifier.size(72.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.graphicsLayer { rotationZ = 90f * swap }) {
                Cells(2, 3, 20.dp) { _, _ -> Yellow }
            }
        }
    }
}

@Composable
private fun FreezeScene() {
    val t = sceneTime(3600)
    val ice = track(t, 0f to 0f, 0.04f to 0f, 0.14f to 1f, 0.92f to 1f, 1f to 0f)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf(2, 3).forEach { value ->
                Box(contentAlignment = Alignment.Center) {
                    DieFace(value = value, size = 56.dp)
                    AroundDie(size = 64.dp) {
                        Box(
                            modifier = Modifier
                                .requiredSize(64.dp)
                                .graphicsLayer { alpha = ice }
                                .background(Ice, RoundedCornerShape(17.dp))
                                .border(3.dp, IceEdge, RoundedCornerShape(17.dp))
                        )
                    }
                }
            }
        }
        LatePop(t, at = 0.22f) {
            Row(
                modifier = Modifier
                    .background(DarkBlue, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = GameIcons.Snowflake,
                    contentDescription = null,
                    tint = IceEdge,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = stringResource(R.string.tutorial_freeze_next, 2, 3),
                    color = White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
private fun DraftScene() {
    val t = sceneTime(3600)
    val gone = track(t, 0f to 0f, 0.38f to 0f, 0.48f to 1f, 0.92f to 1f, 1f to 0f)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Когда лишний кубик исчезает, оставшиеся два съезжают к центру (половина кубика с зазором)
        Row(
            modifier = Modifier.graphicsLayer { translationX = (33f * gone).dp.toPx() },
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Третий кубик прилетает сверху
            DieFace(
                value = 1,
                size = 56.dp,
                modifier = Modifier.graphicsLayer {
                    alpha = track(t, 0f to 0f, 0.02f to 0f, 0.08f to 1f, 0.92f to 1f, 1f to 0f)
                    translationY = track(t, 0f to -70f, 0.02f to -70f, 0.18f to 0f).dp.toPx()
                    rotationZ = track(t, 0f to -30f, 0.02f to -30f, 0.18f to 0f)
                }
            )
            DieFace(value = 3, size = 56.dp)
            // Лишний кубик убирают нажатием
            Box(contentAlignment = Alignment.Center) {
                DieFace(
                    value = 6,
                    size = 56.dp,
                    modifier = Modifier.graphicsLayer {
                        alpha = 1f - gone
                        scaleX = 1f - 0.8f * gone
                        scaleY = 1f - 0.8f * gone
                    }
                )
                TapRing(t, at = 0.28f)
            }
        }
        LatePop(t, at = 0.5f) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    modifier = Modifier.alpha(0.85f),
                    text = stringResource(R.string.tutorial_block, 1, 3),
                    color = White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Cells(1, 3, 22.dp) { _, _ -> Yellow }
            }
        }
    }
}

@Composable
private fun LuckyScene() {
    val t = sceneTime(3600)
    // Сначала выбирается грань, потом кубик, на котором она появится
    val pick = track(t, 0f to 0f, 0.2f to 0f, 0.28f to 1f, 0.92f to 1f, 1f to 0f)
    val newFace = track(t, 0f to 0f, 0.56f to 0f, 0.66f to 1f)
    val newFaceScale = track(t, 0f to 0.6f, 0.56f to 0.6f, 0.66f to 1.12f, 0.72f to 1f)
    val oldFace = track(t, 0f to 1f, 0.56f to 1f, 0.64f to 0f)
    val pickedBg = Yellow.copy(alpha = 0.3f).compositeOver(DarkBlue)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            (1..6).forEach { value ->
                val p = if (value == 1) pick else 0f
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(11.dp))
                        .background(lerp(DarkBlue, pickedBg, p))
                        .border(3.dp, Yellow.copy(alpha = p), RoundedCornerShape(11.dp))
                        .padding(6.dp)
                ) {
                    DieFace(value = value, size = 30.dp)
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(contentAlignment = Alignment.Center) {
                DieFace(
                    value = 6,
                    size = 56.dp,
                    modifier = Modifier.graphicsLayer { alpha = oldFace }
                )
                DieFace(
                    value = 1,
                    size = 56.dp,
                    glow = newFace,
                    modifier = Modifier.graphicsLayer {
                        alpha = newFace
                        scaleX = newFaceScale
                        scaleY = newFaceScale
                    }
                )
                TapRing(t)
            }
            DieFace(value = 4, size = 56.dp)
        }
    }
}

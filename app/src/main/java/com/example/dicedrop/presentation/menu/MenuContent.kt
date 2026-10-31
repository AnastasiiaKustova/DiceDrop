package com.example.dicedrop.presentation.menu

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import com.example.dicedrop.presentation.common.isLandscape
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalContext
import com.example.dicedrop.presentation.game.ui.GameSnackbarHost
import com.example.dicedrop.presentation.shop.ui.RandomBonusDialog
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
import com.example.dicedrop.domain.feedback.GameFeedback
import com.example.dicedrop.presentation.common.LocalGameFeedback
import com.example.dicedrop.presentation.common.CoinChip
import com.example.dicedrop.presentation.common.FlyingCoins
import com.example.dicedrop.presentation.common.DieFace
import com.example.dicedrop.presentation.common.GameButton
import com.example.dicedrop.presentation.common.GameIcons
import com.example.dicedrop.presentation.common.IconCircle
import com.example.dicedrop.presentation.common.RoundImageButton
import com.example.dicedrop.presentation.common.TutorialDialog
import com.example.dicedrop.presentation.common.spaced
import com.example.dicedrop.presentation.ui.theme.DarkBlue
import com.example.dicedrop.presentation.ui.theme.DeepBlue
import com.example.dicedrop.presentation.ui.theme.Green
import com.example.dicedrop.presentation.ui.theme.GreenText
import com.example.dicedrop.presentation.ui.theme.MidBlue
import com.example.dicedrop.presentation.ui.theme.Navy
import com.example.dicedrop.presentation.ui.theme.White
import com.example.dicedrop.presentation.ui.theme.Yellow


@Composable
fun MenuContent(component: MenuComponent) {
    val state by component.model.collectAsState()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val feedback = LocalGameFeedback.current
    val density = LocalDensity.current.density
    val scope = rememberCoroutineScope()
    val coinFlight = remember { FlyingCoins() }

    LaunchedEffect(Unit) {
        component.uiEvents.collect { event ->
            when (event) {
                MenuComponent.UiEvent.GiftAlreadyReceived -> launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.daily_gift_already_received),
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Menu(
            state = state.copy(coins = coinFlight.coinsToShow(state.coinsBeforeGift ?: state.coins)),
            coinsModifier = with(coinFlight) { Modifier.coinsTarget() },
            onStartGameClick = { component.onStartGameClick() },
            onAchievementsClick = { component.onAchievementsClick() },
            onShopClick = { component.onShopClick() },
            onSettingsClick = { component.onSettingsClick() },
            onGiftClick = { component.onGiftClick() },
        )

        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            GameSnackbarHost(
                snackbarHostState = snackbarHostState,
                bottomPadding = 64.dp,
                iconRes = R.drawable.ic_gift_box,
                showAlert = false
            )
        }

        state.receivedGift?.let { gift ->
            LaunchedEffect(gift) { feedback.play(GameFeedback.Sound.DAILY_GIFT) }
            RandomBonusDialog(
                bonuses = gift,
                onCoinsFly = { from, amount ->
                    coinFlight.holdCoins(state.coinsBeforeGift ?: (state.coins - amount))
                    scope.launch { coinFlight.flyReward(from, amount, density, feedback) }
                },
                confirmClick = { component.onGiftConfirm() }
            )
        }

        coinFlight.Layer()

        // При первом запуске; «Играть!» на последнем шаге сразу начинает игру
        if (state.showTutorial) {
            TutorialDialog(
                onClose = { component.onTutorialClose() },
                onFinish = {
                    component.onTutorialFinish()
                    component.onStartGameClick()
                }
            )
        }
    }
}

@Composable
private fun Menu(
    state: MenuStore.State,
    // Для счётчика монет: сюда летят монеты подарка
    coinsModifier: Modifier = Modifier,
    onStartGameClick: () -> Unit,
    onAchievementsClick: () -> Unit,
    onShopClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onGiftClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlue)
    ) {
        BackgroundDecor()

        if (isLandscape()) {
            MenuLandscape(
                state = state,
                coinsModifier = coinsModifier,
                onStartGameClick = onStartGameClick,
                onAchievementsClick = onAchievementsClick,
                onShopClick = onShopClick,
                onSettingsClick = onSettingsClick,
                onGiftClick = onGiftClick
            )
        } else Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CoinChip(coins = state.coins, modifier = coinsModifier)
                Spacer(modifier = Modifier.weight(1f))
                RoundImageButton(
                    iconRes = R.drawable.btn_settings,
                    contentDescription = stringResource(R.string.settings_title),
                    size = 44.dp,
                    onClick = onSettingsClick
                )
            }

            Spacer(modifier = Modifier.weight(0.6f).sizeIn(maxHeight = 60.dp))

            Logo()

            Spacer(modifier = Modifier.height(36.dp))

            RecordCard(bestScore = state.bestScore)

            Spacer(modifier = Modifier.height(22.dp))

            GameButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.play),
                onClick = onStartGameClick,
                height = 76.dp,
                corner = 24.dp,
                fontSize = 30.sp,
                icon = GameIcons.Play,
                iconSize = 26.dp
            )

            Spacer(modifier = Modifier.height(18.dp))

            MenuTiles(
                state = state,
                horizontal = false,
                onShopClick = onShopClick,
                onAchievementsClick = onAchievementsClick,
                onGiftClick = onGiftClick
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                modifier = Modifier.alpha(0.55f),
                text = stringResource(R.string.daily_streak, state.streakDay),
                color = White,
                fontSize = 12.sp
            )
        }
    }
}

// Планшет: всё одним блоком по центру - логотип строкой, рекорд рядом с «Играть», плитки в ряд
@Composable
private fun BoxScope.MenuLandscape(
    state: MenuStore.State,
    coinsModifier: Modifier,
    onStartGameClick: () -> Unit,
    onAchievementsClick: () -> Unit,
    onShopClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onGiftClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp, vertical = 28.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CoinChip(coins = state.coins, modifier = coinsModifier)
        Spacer(modifier = Modifier.weight(1f))
        RoundImageButton(
            iconRes = R.drawable.btn_settings,
            contentDescription = stringResource(R.string.settings_title),
            size = 48.dp,
            onClick = onSettingsClick
        )
    }

    Column(
        modifier = Modifier
            .align(Alignment.Center)
            .padding(top = 40.dp)
            .widthIn(max = 780.dp)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            DieFace(value = 5, size = 84.dp, modifier = Modifier.rotate(-10f))
            LogoTitle(fontSize = 76)
            DieFace(value = 3, size = 84.dp, gold = true, modifier = Modifier.rotate(8f))
        }

        Spacer(modifier = Modifier.height(26.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            RecordCard(
                bestScore = state.bestScore,
                modifier = Modifier
                    .width(260.dp)
                    .fillMaxHeight()
            )
            GameButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.play),
                onClick = onStartGameClick,
                height = 100.dp,
                corner = 26.dp,
                fontSize = 38.sp,
                icon = GameIcons.Play,
                iconSize = 34.dp
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        MenuTiles(
            state = state,
            horizontal = true,
            onShopClick = onShopClick,
            onAchievementsClick = onAchievementsClick,
            onGiftClick = onGiftClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            modifier = Modifier.alpha(0.55f),
            text = stringResource(R.string.daily_streak, state.streakDay),
            color = White,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun MenuTiles(
    state: MenuStore.State,
    horizontal: Boolean,
    onShopClick: () -> Unit,
    onAchievementsClick: () -> Unit,
    onGiftClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(if (horizontal) 18.dp else 12.dp)
    ) {
        MenuTile(
            modifier = Modifier.weight(1f),
            iconRes = R.drawable.ic_shop_bag,
            title = stringResource(R.string.store),
            horizontal = horizontal,
            onClick = onShopClick
        )
        MenuTile(
            modifier = Modifier.weight(1f),
            iconRes = R.drawable.ic_trophy,
            title = stringResource(R.string.achievements),
            titleSize = 13,
            horizontal = horizontal,
            onClick = onAchievementsClick
        ) {
            if (state.rewardsToClaim > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 6.dp, y = (-8).dp)
                        .sizeIn(minWidth = 26.dp)
                        .height(26.dp)
                        .background(Yellow, CircleShape)
                        .padding(horizontal = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.rewardsToClaim.toString(),
                        color = Navy,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
        MenuTile(
            modifier = Modifier.weight(1f),
            // Подарок за сегодня уже получен - коробка открыта, следующий завтра
            iconRes = if (state.giftReady) R.drawable.ic_gift_box else R.drawable.ic_gift_box_open,
            title = stringResource(R.string.gift),
            horizontal = horizontal,
            onClick = onGiftClick
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-11).dp)
                    .alpha(if (state.giftReady) 1f else 0.9f)
                    .background(if (state.giftReady) Green else DeepBlue, RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 2.dp),
                text = stringResource(if (state.giftReady) R.string.gift_ready else R.string.gift_tomorrow),
                color = if (state.giftReady) GreenText else White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun Logo() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            DieFace(value = 5, size = 70.dp, modifier = Modifier.rotate(-10f))
            DieFace(
                value = 3,
                size = 70.dp,
                gold = true,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .rotate(8f)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        LogoTitle(fontSize = 50)
    }
}

@Composable
private fun LogoTitle(fontSize: Int) {
    Text(
        text = buildAnnotatedString {
            append("DICE ")
            withStyle(SpanStyle(color = Yellow)) { append("DROP") }
        },
        color = White,
        fontSize = fontSize.sp,
        lineHeight = fontSize.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 1.sp
    )
}

@Composable
private fun RecordCard(bestScore: Int, modifier: Modifier = Modifier.fillMaxWidth()) {
    Column(
        modifier = modifier
            .background(MidBlue, RoundedCornerShape(20.dp))
            .padding(top = 12.dp, bottom = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.alpha(0.85f),
            text = stringResource(R.string.record_caps),
            color = White,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.5.sp
        )
        Text(
            text = bestScore.spaced(),
            color = White,
            fontSize = 32.sp,
            lineHeight = 34.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
private fun MenuTile(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    title: String,
    titleSize: Int = 14,
    // На планшете плитка ниже и шире: иконка слева от подписи
    horizontal: Boolean = false,
    onClick: () -> Unit,
    badge: @Composable BoxScope.() -> Unit = {}
) {
    val feedback = LocalGameFeedback.current
    val tileModifier = Modifier
        .fillMaxSize()
        .clip(RoundedCornerShape(if (horizontal) 26.dp else 22.dp))
        .background(MidBlue)
        .clickable {
            feedback.play(GameFeedback.Sound.BUTTON_CLICK)
            onClick()
        }
    Box(modifier = modifier.height(if (horizontal) 104.dp else 116.dp)) {
        if (horizontal) {
            Row(
                modifier = tileModifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterHorizontally)
            ) {
                IconCircle(iconRes = iconRes, size = 70.dp, iconSize = 50.dp)
                Text(
                    text = title,
                    color = White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1
                )
            }
        } else {
            Column(
                modifier = tileModifier.padding(top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
            ) {
                IconCircle(iconRes = iconRes, size = 64.dp, iconSize = 46.dp)
                Text(
                    text = title,
                    color = White,
                    fontSize = titleSize.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1
                )
            }
        }
        badge()
    }
}

// Полупрозрачные "кубики" на фоне, как в макете
@Composable
private fun BackgroundDecor() {
    Box(modifier = Modifier.fillMaxSize()) {
        DecorSquare(Alignment.TopStart, (-30).dp, 150.dp, 90.dp, MidBlue, 0.35f, -14f)
        DecorSquare(Alignment.TopEnd, 24.dp, 300.dp, 70.dp, Yellow, 0.16f, 18f)
        DecorSquare(Alignment.BottomStart, 30.dp, (-60).dp, 46.dp, MidBlue, 0.35f, 10f)
        DecorSquare(Alignment.BottomEnd, (-40).dp, (-140).dp, 34.dp, Yellow, 0.16f, -8f)
    }
}

@Composable
private fun androidx.compose.foundation.layout.BoxScope.DecorSquare(
    alignment: Alignment,
    x: Dp,
    y: Dp,
    size: Dp,
    color: Color,
    alpha: Float,
    rotation: Float
) {
    Box(
        modifier = Modifier
            .align(alignment)
            .offset(x = x, y = y)
            .size(size)
            .rotate(rotation)
            .alpha(alpha)
            .background(color, RoundedCornerShape(size * 0.24f))
    )
}

@Preview(widthDp = 390, heightDp = 844)
@Composable
fun MenuPreview() {
    Menu(
        state = MenuStore.State(coins = 1250, bestScore = 12450, rewardsToClaim = 2, giftReady = true, streakDay = 3),
        onStartGameClick = { },
        onAchievementsClick = { },
        onShopClick = { },
        onSettingsClick = { },
        onGiftClick = { },
    )
}

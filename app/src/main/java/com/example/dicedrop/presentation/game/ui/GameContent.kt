package com.example.dicedrop.presentation.game.ui

import GameStateContent
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
import com.example.dicedrop.domain.ads.RewardedAdManager
import com.example.dicedrop.domain.entity.AdReward
import com.example.dicedrop.domain.feedback.GameFeedback
import com.example.dicedrop.domain.feedback.GameFeedback.Companion.lineCleared
import com.example.dicedrop.presentation.common.ConfettiBurst
import com.example.dicedrop.presentation.common.DialogScrim
import com.example.dicedrop.presentation.common.LocalGameFeedback
import com.example.dicedrop.presentation.common.isLandscape
import com.example.dicedrop.presentation.game.GameComponent
import com.example.dicedrop.presentation.game.ui.board.ComboPopup
import com.example.dicedrop.presentation.game.ui.bottompanel.getPrice
import com.example.dicedrop.presentation.game.ui.bottompanel.getTitleResId
import com.example.dicedrop.presentation.ui.theme.AccentText
import com.example.dicedrop.presentation.ui.theme.ButtonText
import com.example.dicedrop.presentation.ui.theme.Green
import com.example.dicedrop.presentation.ui.theme.LightBlue
import com.example.dicedrop.presentation.ui.theme.MutedText
import com.example.dicedrop.presentation.ui.theme.Orange
import com.example.dicedrop.presentation.ui.theme.White
import com.example.dicedrop.presentation.ui.theme.Yellow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Баннер полученного достижения; id нужен, чтобы два одинаковых события подряд тоже показались
data class AchievementBanner(
    val titleId: Int,
    val descriptionId: Int,
    val reward: Int,
    val id: Long = System.nanoTime()
)

@Composable
fun GameContent(component: GameComponent) {

    val context = LocalContext.current
    val feedback = LocalGameFeedback.current

    val state by component.model.collectAsState()

    val rewardedAdManager = component.rewardedAdManager

    var clearedRows by remember { mutableStateOf<List<Int>>(emptyList()) }
    var clearedCols by remember { mutableStateOf<List<Int>>(emptyList()) }
    var comboPopup by remember { mutableStateOf<ComboPopup?>(null) }
    var banner by remember { mutableStateOf<AchievementBanner?>(null) }
    var bannerVisible by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Осколки и монеты при очистке линий; играют сами по себе, не задерживая события
    val clearEffects = remember { ClearEffects() }
    val effectsScope = rememberCoroutineScope()
    val density = LocalDensity.current.density

    val adState by component
        .rewardedAdManager
        .state
        .collectAsState()

    LaunchedEffect(Unit) {
        component.uiEvents.collect { event ->
            // Уведомление висит несколько секунд, поэтому каждое событие обрабатывается отдельно,
            // чтобы не блокировать поток событий (навигацию, рекламу)
            launch {
                handleUiEvent(
                    event = event,
                    context = context,
                    feedback = feedback,
                    rewardedAdManager = rewardedAdManager,
                    snackbarHostState = snackbarHostState,
                    onClearLine = { clearEvent ->
                        clearedRows = clearEvent.clearedRows
                        clearedCols = clearEvent.clearedCols
                        comboPopup = ComboPopup(
                            combo = clearEvent.combo,
                            points = clearEvent.points,
                            coins = clearEvent.coins
                        )
                        effectsScope.launch {
                            clearEffects.play(
                                rows = clearEvent.clearedRows,
                                cols = clearEvent.clearedCols,
                                coinsEarned = clearEvent.coins,
                                density = density,
                                feedback = feedback
                            )
                        }
                    },
                    onClearFinished = {
                        clearedRows = emptyList()
                        clearedCols = emptyList()
                    },
                    onAdRewarded = { reward ->
                        if (reward == AdReward.DOUBLE_COINS) feedback.play(GameFeedback.Sound.COINS_REWARD)
                        component.AdRewardReceive(reward)
                    },
                    onAdUnavailable = {
                        // Ответ рекламы приходит позже, когда обработка этого события уже закончилась,
                        // поэтому не её scope: в завершённом сообщение отменилось бы, не появившись
                        effectsScope.launch {
                            snackbarHostState.currentSnackbarData?.dismiss()
                            snackbarHostState.showSnackbar(context.getString(R.string.ad_unavailable))
                        }
                    },
                    onAchievement = {
                        banner = it
                        bannerVisible = true
                    },
                    onGoToShop = { component.onShopClick() }
                )
            }
        }
    }

    // Баннер держится 3 секунды, новое достижение перезапускает таймер
    LaunchedEffect(banner?.id) {
        if (banner != null) {
            delay(3000)
            bannerVisible = false
        }
    }

    BackHandler {
        component.onBackPressed()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        GameStateContent(
            state = state.gameState,
            clearedRows = clearedRows,
            clearedCols = clearedCols,
            comboPopup = comboPopup,
            trophyHighlighted = bannerVisible,
            rewardsToClaim = state.rewardsToClaim,
            settings = state.settings,
            clearEffects = clearEffects,
            component = component
        )

        clearEffects.Overlay()

        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.TopCenter)
                // На планшете шапки над полем нет, баннер встаёт у верхнего края
                .padding(top = if (isLandscape()) 24.dp else 96.dp)
                .widthIn(max = 560.dp),
            visible = bannerVisible,
            enter = slideInVertically { -it } + fadeIn(),
            exit = slideOutVertically { -it } + fadeOut()
        ) {
            banner?.let { AchievementBannerCard(it) }
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            GameSnackbarHost(snackbarHostState, bottomPadding = 150.dp)
        }

        if (adState != RewardedAdManager.RewardedAdState.Idle) {
            RewardedAdOverlay(adState)
        }
    }
}

// internal: этот же баннер показывает промо-ролик в отладочной сборке
@Composable
internal fun AchievementBannerCard(banner: AchievementBanner) {
    Box(
        modifier = Modifier
            .padding(horizontal = 14.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(14.dp, RoundedCornerShape(22.dp))
                .background(White, RoundedCornerShape(22.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_trophy),
                contentDescription = null,
                modifier = Modifier.size(54.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.achievement_banner),
                    color = AccentText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = stringResource(banner.titleId),
                    color = ButtonText,
                    fontSize = 19.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = stringResource(banner.descriptionId),
                    color = MutedText,
                    fontSize = 12.sp
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.coin_star),
                    contentDescription = null,
                    modifier = Modifier.size(26.dp)
                )
                Text(
                    text = "+${banner.reward}",
                    color = ButtonText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
        // Конфетти вылетает из баннера при его появлении и осыпается на поле
        ConfettiBurst(
            key = banner.id,
            modifier = Modifier.matchParentSize(),
            count = 40,
            power = 0.9f
        )
    }
}

@Composable
fun RewardedAdOverlay(state: RewardedAdManager.RewardedAdState) {
    DialogScrim {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            CircularProgressIndicator(color = Yellow)

            Spacer(Modifier.height(16.dp))

            Text(
                text = when (state) {
                    RewardedAdManager.RewardedAdState.Loading -> stringResource(R.string.loading_ad)
                    RewardedAdManager.RewardedAdState.Showing -> stringResource(R.string.watching_ad)
                    RewardedAdManager.RewardedAdState.Rewarded -> stringResource(R.string.reward_received_ad)
                    else -> ""
                },
                color = White,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

private suspend fun handleUiEvent(
    event: GameComponent.UiEvent,
    context: Context,
    feedback: GameFeedback,
    rewardedAdManager: RewardedAdManager,
    snackbarHostState: SnackbarHostState,
    onClearLine: (GameComponent.UiEvent.ClearLine) -> Unit,
    onClearFinished: () -> Unit,
    onAdRewarded: (AdReward) -> Unit,
    onAdUnavailable: () -> Unit,
    onAchievement: (AchievementBanner) -> Unit,
    onGoToShop: () -> Unit
) {

    when (event) {
        is GameComponent.UiEvent.ClearLine -> {
            feedback.lineCleared(event.combo)
            onClearLine(event)
            delay(400)
            onClearFinished()
        }

        is GameComponent.UiEvent.Error -> {
            // Для нехватки монет второй строкой пишем, какой бонус и сколько он стоит
            val details = event.bonusType?.let { type ->
                val price = type.getPrice()
                "\n" + context.resources.getQuantityString(
                    R.plurals.bonus_price_coins,
                    price,
                    context.getString(type.getTitleResId()),
                    price
                )
            } ?: ""
            feedback.play(GameFeedback.Sound.NOT_ENOUGH_COINS)
            snackbarHostState.currentSnackbarData?.dismiss()
            val result = snackbarHostState.showSnackbar(
                message = context.getString(event.msg.stringRes) + details,
                actionLabel = context.getString(R.string.go_to_shop),
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) onGoToShop()
        }

        is GameComponent.UiEvent.AchievementUnlocked -> {
            feedback.play(GameFeedback.Sound.ACHIEVEMENT)
            feedback.vibrate(GameFeedback.Haptic.CELEBRATE)
            onAchievement(
                AchievementBanner(
                    titleId = event.titleId,
                    descriptionId = event.descriptionId,
                    reward = event.reward
                )
            )
        }

        GameComponent.UiEvent.BonusBought -> feedback.play(GameFeedback.Sound.BONUS_BUY)

        GameComponent.UiEvent.Animate -> Unit
        is GameComponent.UiEvent.WatchAd -> {
            val activity = context.findActivity() ?: return
            rewardedAdManager.showAd(
                activity = activity,
                reward = event.reward,
                onReward = { onAdRewarded(event.reward) },
                onUnavailable = onAdUnavailable
            )
        }
    }
}

// Ролик Яндекса показывается поверх Activity, а LocalContext может быть обёрткой над ней
tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

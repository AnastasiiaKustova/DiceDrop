package com.example.dicedrop.presentation.game.ui

import GameStateContent
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
import com.example.dicedrop.domain.ads.RewardedAdManager
import com.example.dicedrop.presentation.game.GameComponent
import kotlinx.coroutines.delay

@Composable
fun GameContent(component: GameComponent) {

    val context = LocalContext.current

    val state by component.model.collectAsState()

    val rewardedAdManager = component.rewardedAdManager

    var clearedRows by remember { mutableStateOf<List<Int>>(emptyList()) }
    var clearedCols by remember { mutableStateOf<List<Int>>(emptyList()) }

    val snackbarHostState = remember { SnackbarHostState() }

    val adState by component
        .rewardedAdManager
        .state
        .collectAsState()

    LaunchedEffect(Unit) {
        component.uiEvents.collect { event ->
            handleUiEvent(
                event = event,
                context = context,
                rewardedAdManager = rewardedAdManager,
                snackbarHostState = snackbarHostState,
                onClearLine = { rows, cols ->
                    clearedRows = rows
                    clearedCols = cols
                },
                onClearFinished = {
                    clearedRows = emptyList()
                    clearedCols = emptyList()
                },
                onAdRewarded = {
                    component.AdRewardReceive()
                }
            )
        }
    }

    BackHandler {
        component.onBackPressed()
    }

    Scaffold(
        snackbarHost = { GameSnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            GameStateContent(
                state = state.gameState,
                clearedRows = clearedRows,
                clearedCols = clearedCols,
                component = component
            )

            if (adState != RewardedAdManager.RewardedAdState.Idle) {
                RewardedAdOverlay(adState)
            }
        }
    }
}

@Composable
fun RewardedAdOverlay(state: RewardedAdManager.RewardedAdState) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .pointerInput(Unit) {},
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            CircularProgressIndicator(color = Color.White)

            Spacer(Modifier.height(16.dp))

            Text(
                text = when (state) {
                    RewardedAdManager.RewardedAdState.Loading -> stringResource(R.string.loading_ad)
                    RewardedAdManager.RewardedAdState.Showing -> stringResource(R.string.watching_ad)
                    RewardedAdManager.RewardedAdState.Rewarded -> stringResource(R.string.reward_received_ad)
                    else -> ""
                },
                color = Color.White,
                fontSize = 18.sp
            )
        }
    }
}

private suspend fun handleUiEvent(
    event: GameComponent.UiEvent,
    context: Context,
    rewardedAdManager: RewardedAdManager,
    snackbarHostState: SnackbarHostState,
    onClearLine: (List<Int>, List<Int>) -> Unit,
    onClearFinished: () -> Unit,
    onAdRewarded: () -> Unit
) {

    when (event) {
        is GameComponent.UiEvent.ClearLine -> {
            onClearLine(event.clearedRows, event.clearedCols)
            delay(400)
            onClearFinished()
        }

        is GameComponent.UiEvent.Error -> {
            snackbarHostState.showSnackbar(
                message = context.getString(event.msg.stringRes),
                actionLabel = context.getString(R.string.go_to_shop),
                duration = SnackbarDuration.Short
            )
        }

        GameComponent.UiEvent.Animate -> Unit
        GameComponent.UiEvent.WatchAd -> {
            rewardedAdManager.showAd {
                onAdRewarded()
            }
        }
    }
}
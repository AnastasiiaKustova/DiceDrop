package com.example.dicedrop.presentation.achievements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import com.example.dicedrop.domain.feedback.GameFeedback
import com.example.dicedrop.presentation.achievements.animation.RewardEffects
import com.example.dicedrop.presentation.achievements.ui.AchievementTopBar
import com.example.dicedrop.presentation.achievements.ui.AchievementsList
import com.example.dicedrop.presentation.common.LocalGameFeedback
import com.example.dicedrop.presentation.ui.theme.DarkBlue
import com.example.dicedrop.presentation.ui.theme.White

@Composable
fun AchievementsContent(component: AchievementsComponent) {
    val state by component.model.collectAsState()

    val rewardEffects = remember { RewardEffects() }
    val scope = rememberCoroutineScope()
    val feedback = LocalGameFeedback.current
    val density = LocalDensity.current.density

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlue)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AchievementTopBar(
                coins = state.coins?.let(rewardEffects.coinFlight::coinsToShow),
                coinsModifier = with(rewardEffects.coinFlight) { Modifier.coinsTarget() },
                onBackClick = { component.onBackClick() }
            )

            when (val achievementsState = state.achievementsState) {
                AchievementsStore.State.AchievementsState.Error -> Unit
                AchievementsStore.State.AchievementsState.Initial -> Unit
                is AchievementsStore.State.AchievementsState.Loaded -> AchievementsList(
                    achievements = achievementsState.achievements,
                    rewardStages = rewardEffects.stages,
                    onGetRewardClick = { claim ->
                        if (claim.achievementId !in rewardEffects.stages) {
                            feedback.play(GameFeedback.Sound.COINS_REWARD)
                            rewardEffects.play(scope, claim, state.coins ?: 0, density, feedback)
                            component.onGetRewardClick(claim.achievementId)
                        }
                    }
                )
                AchievementsStore.State.AchievementsState.Loading -> Loading()
            }
        }

        rewardEffects.Overlay()
    }
}

@Composable
private fun Loading() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            color = White
        )
    }
}

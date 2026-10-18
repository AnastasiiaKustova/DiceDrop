package com.example.dicedrop.presentation.achievements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.dicedrop.presentation.achievements.animation.ConfettiOverlay
import com.example.dicedrop.presentation.achievements.animation.RewardAnimationData
import com.example.dicedrop.presentation.achievements.ui.AchievementTopBar
import com.example.dicedrop.presentation.achievements.ui.AchievementsList
import com.example.dicedrop.presentation.ui.theme.DarkBlue
import com.example.dicedrop.presentation.ui.theme.White
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AchievementsContent(component: AchievementsComponent) {
    val state by component.model.collectAsState()

    var rewardAnimation by remember { mutableStateOf<RewardAnimationData?>(null) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlue)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AchievementTopBar(onBackClick = { component.onBackClick() })

            when (val achievementsState = state.achievementsState) {
                AchievementsStore.State.AchievementsState.Error -> Unit
                AchievementsStore.State.AchievementsState.Initial -> Unit
                is AchievementsStore.State.AchievementsState.Loaded -> AchievementsList(
                    achievements = achievementsState.achievements,
                    onGetRewardClick = { achievementId, cardCenter ->

                        rewardAnimation = RewardAnimationData(
                            achievementId = achievementId,
                            start = cardCenter
                        )

                        // Награду забираем, когда конфетти уже разлетелось, а не после всей анимации
                        scope.launch {
                            delay(500)
                            component.onGetRewardClick(achievementId)
                        }
                    }
                )
                AchievementsStore.State.AchievementsState.Loading -> Loading()
            }
        }

        ConfettiOverlay(
            data = rewardAnimation,
            onFinished = { rewardAnimation = null }
        )
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

package com.example.dicedrop.presentation.achievements

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.dicedrop.domain.entity.achievement.Achievement
import com.example.dicedrop.presentation.achievements.animation.ConfettiOverlay
import com.example.dicedrop.presentation.achievements.animation.RewardAnimationData
import com.example.dicedrop.presentation.achievements.ui.AchievementsList
import com.example.dicedrop.presentation.achievements.ui.AchievementTopBar
import com.example.dicedrop.presentation.ui.theme.LightBlue

@Composable
fun AchievementsContent(component: AchievementsComponent) {
    val state by component.model.collectAsState()

    var rewardAnimation by remember { mutableStateOf<RewardAnimationData?>(null) }
    var rewardAnimationFinished by remember { mutableStateOf<(() -> Unit)?>(null) }

    Scaffold(
        topBar = {
            AchievementTopBar(onBackClick = { component.onBackClick() })
        },
        containerColor = LightBlue,
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val achievementsState = state.achievementsState) {
                AchievementsStore.State.AchievementsState.Error -> Error()
                AchievementsStore.State.AchievementsState.Initial -> {}
                is AchievementsStore.State.AchievementsState.Loaded -> Loaded(
                    achievements = achievementsState.achievements,
                    onGetRewardClick = { achievementId, cardCenter ->

                        rewardAnimation = RewardAnimationData(
                            achievementId = achievementId,
                            start = cardCenter
                        )

                        rewardAnimationFinished = {
                            component.onGetRewardClick(achievementId)
                            rewardAnimation = null
                            rewardAnimationFinished = null
                        }
                    }
                )
                AchievementsStore.State.AchievementsState.Loading -> Loading()
            }

            ConfettiOverlay(
                data = rewardAnimation,
                onFinished = {
                    rewardAnimationFinished?.invoke()
                }
            )
        }
    }
}

@Composable
private fun Loaded(
    achievements: List<Achievement>,
    onGetRewardClick: (id: String, offset: Offset) -> Unit
) {
    AchievementsList(
        achievements = achievements,
        onGetRewardClick = onGetRewardClick,
    )
}

@Composable
private fun Error() {

}

@Composable
private fun Loading() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            color = Color.White
        )
    }
}
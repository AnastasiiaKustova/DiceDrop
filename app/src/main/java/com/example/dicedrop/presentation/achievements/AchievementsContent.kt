package com.example.dicedrop.presentation.achievements

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dicedrop.domain.entity.Achievement
import com.example.dicedrop.presentation.achievements.animation.ConfettiOverlay
import com.example.dicedrop.presentation.achievements.animation.RewardAnimationData
import com.example.dicedrop.presentation.ui.theme.WhiteBlue

@Composable
fun AchievementsContent(component: AchievementsComponent) {
    val state by component.model.collectAsState()

    var rewardAnimation by remember { mutableStateOf<RewardAnimationData?>(null) }
    var rewardAnimationFinished by remember { mutableStateOf<(() -> Unit)?>(null) }

    // scale карточки для анимации
    val scale by animateFloatAsState(
        targetValue = if (rewardAnimation != null) 1.1f else 1f,
        animationSpec = tween(500)
    )

    Scaffold(
        topBar = {
            TopBar(onBackClick = { component.onBackClick() })
        },
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val achievementsState = state.achievementsState) {
                AchievementsStore.State.AchievementsState.Error -> Error()
                AchievementsStore.State.AchievementsState.Initial -> Initial()
                is AchievementsStore.State.AchievementsState.Loaded -> {
                    AchievementsList(
                        achievements = achievementsState.achievements,
                        onGetRewardClick = { achievementId, cardCenter ->
                            // Запускаем анимацию
                            rewardAnimation = RewardAnimationData(
                                achievementId = achievementId,
                                start = cardCenter
                            )
                            // Задаём что сделать после окончания анимации
                            rewardAnimationFinished = {
                                component.onGetRewardClick(achievementId)
                                rewardAnimation = null
                                rewardAnimationFinished = null
                            }
                        },
                    )
                }
                AchievementsStore.State.AchievementsState.Loading -> Loading()
            }

            // Confetti overlay поверх всех карточек
            ConfettiOverlay(
                data = rewardAnimation,
                onFinished = {
                    // Вызываем callback после окончания анимации
                    rewardAnimationFinished?.invoke()
                }
            )
        }
    }
}

@Composable
private fun Initial() {

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
            color = MaterialTheme.colorScheme.background
        )
    }
}

@Composable
private fun AchievementsList(
    achievements: List<Achievement>,
    onGetRewardClick: (achievementID: String, Offset) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(
            items = achievements,
            key = {
                it.id
            }
        ) {
            AchievementBox(
                achievementID = it.id,
                title = it.title,
                description = it.description,
                currentProgress = it.totalCount,
                totalProgress = it.requiredCount,
                reward = it.reward,
                isClaimed = it.isRewardClaimed,
                onGetRewardClick = { achievementId, cardCenter ->
                    onGetRewardClick(achievementId, cardCenter)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
) {
    CenterAlignedTopAppBar(
        modifier = modifier
            .background(color = WhiteBlue),
        title = { Text(text = "Achievements") },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors().copy(
            containerColor = Color.White
        ),
        navigationIcon = {
            IconButton(onClick = { onBackClick() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.background
                )
            }
        }
    )
}
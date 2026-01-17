package com.example.dicedrop.presentation.achievements.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.example.dicedrop.domain.entity.Achievement
import com.example.dicedrop.presentation.game.ui.bottompanel.getIconResId

@Composable
fun AchievementsList(
    achievements: List<Achievement>,
    onGetRewardClick: (achievementID: String, Offset) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
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
                },
                additionalRewards = {
                    if (it.additionalRewards.isNotEmpty()) {
                        for (additionalReward in it.additionalRewards){
                            AnimatedRewardTransition(
                                reward = additionalReward.amount,
                                icCoinsId = additionalReward.bonusType.getIconResId()
                            )
                        }
                    }

                }
            )
        }
    }
}
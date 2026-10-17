package com.example.dicedrop.presentation.achievements.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
import com.example.dicedrop.presentation.animations.AnimatedButton
import com.example.dicedrop.presentation.ui.theme.DarkBlue

@Composable
fun AchievementBox(
    modifier: Modifier = Modifier,
    achievementID: String,
    titleId: Int,
    descriptionId: Int,
    currentProgress: Int,
    totalProgress: Int,
    reward: Int,
    isClaimed: Boolean,
    onGetRewardClick: (achievementID: String, Offset) -> Unit,
    additionalRewards: @Composable () -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        Column(
            modifier = Modifier
                .background(White)
                .padding(top = 16.dp, start = 20.dp, end = 20.dp)
        ) {

            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AchievementTitle(
                    title = stringResource(titleId),
                    description = stringResource(descriptionId),
                )

                if (!isClaimed){
                    additionalRewards()

                    AnimatedRewardTransition(
                        reward = reward,
                    )
                }
                else{
                    Text(
                        text = stringResource(R.string.completed),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = DarkBlue,
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .background(White)
                .padding(top = 8.dp, start = 20.dp, end = 20.dp, bottom = 16.dp)
        ) {
            if (!isClaimed) {
                if (currentProgress >= totalProgress)
                    AnimatedButton(
                        text = stringResource(R.string.claim),
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { cardCenter ->
                            onGetRewardClick(
                                achievementID,
                                cardCenter
                            )
                        }
                    )
                else {
                    ContextWithProgress(
                        current = currentProgress,
                        total = totalProgress
                    )
                }
            }
        }
    }
}
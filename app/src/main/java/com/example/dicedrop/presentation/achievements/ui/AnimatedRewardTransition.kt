package com.example.dicedrop.presentation.achievements.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dicedrop.R

@Composable
fun AnimatedRewardTransition(
    reward: Int,
    icCoinsId: Int = R.drawable.ic_coins
) {

    Box(
        modifier = Modifier.offset(y = 4.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        Row {
            IconWithCoinsCount(
                reward = reward,
                icCoinsId = icCoinsId,
            )
        }
    }
}
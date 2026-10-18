package com.example.dicedrop.presentation.achievements.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
import com.example.dicedrop.domain.entity.achievement.Achievement
import com.example.dicedrop.presentation.common.IconCircle
import com.example.dicedrop.presentation.common.ProgressBar
import com.example.dicedrop.presentation.ui.theme.ButtonText
import com.example.dicedrop.presentation.ui.theme.DeepBlue
import com.example.dicedrop.presentation.ui.theme.MidBlue
import com.example.dicedrop.presentation.ui.theme.White
import com.example.dicedrop.presentation.ui.theme.Yellow

private enum class AchievementTab { ALL, CLAIM, PROGRESS, DONE }

@Composable
fun AchievementsList(
    achievements: List<Achievement>,
    onGetRewardClick: (achievementID: String, Offset) -> Unit,
) {
    var tab by rememberSaveable { mutableStateOf(AchievementTab.ALL) }

    val completed = achievements.count { it.isCompleted }
    val waiting = achievements.count { it.canClaim }

    // Во вкладке "Все" сначала то, что можно забрать, полученные - в конце
    val shown = when (tab) {
        AchievementTab.ALL -> achievements.sortedBy {
            when {
                it.canClaim -> 0
                !it.isRewardClaimed -> 1
                else -> 2
            }
        }
        AchievementTab.CLAIM -> achievements.filter { it.canClaim }
        AchievementTab.PROGRESS -> achievements.filter { !it.isCompleted }
        AchievementTab.DONE -> achievements.filter { it.isRewardClaimed }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 20.dp)
    ) {
        item {
            Summary(completed = completed, total = achievements.size, waiting = waiting)
        }
        item {
            Row(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TabChip(stringResource(R.string.tab_all), tab == AchievementTab.ALL) { tab = AchievementTab.ALL }
                TabChip(stringResource(R.string.tab_claim, waiting), tab == AchievementTab.CLAIM) { tab = AchievementTab.CLAIM }
                TabChip(stringResource(R.string.tab_progress), tab == AchievementTab.PROGRESS) { tab = AchievementTab.PROGRESS }
                TabChip(stringResource(R.string.tab_done), tab == AchievementTab.DONE) { tab = AchievementTab.DONE }
            }
        }
        items(
            items = shown,
            key = { it.id }
        ) {
            AchievementBox(
                achievement = it,
                onGetRewardClick = onGetRewardClick
            )
        }
    }
}

@Composable
private fun Summary(completed: Int, total: Int, waiting: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MidBlue, RoundedCornerShape(22.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        IconCircle(iconRes = R.drawable.ic_trophy, size = 54.dp, iconSize = 41.dp)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = stringResource(R.string.achievements_done),
                color = White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )
            ProgressBar(
                fraction = completed.toFloat() / total.coerceAtLeast(1),
                color = Yellow
            )
            // Как и в карточках, счётчик - под полосой; рядом - сколько наград ждёт
            val waitingText = if (waiting > 0) {
                " · " + pluralStringResource(R.plurals.rewards_waiting, waiting, waiting)
            } else ""
            Text(
                modifier = Modifier.alpha(0.85f),
                text = "$completed / $total$waitingText",
                color = White,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun TabChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        modifier = Modifier
            .height(36.dp)
            .clip(CircleShape)
            .background(if (selected) Yellow else DeepBlue)
            .clickable(onClick = onClick)
            .padding(horizontal = 11.dp, vertical = 7.dp),
        text = label,
        color = if (selected) ButtonText else White,
        fontSize = 14.sp,
        fontWeight = FontWeight.ExtraBold,
        maxLines = 1
    )
}

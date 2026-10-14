package com.example.dicedrop.presentation.game.ui.popups

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
import com.example.dicedrop.domain.entity.BonusItem
import com.example.dicedrop.presentation.achievements.ui.IconWithCoinsCount
import com.example.dicedrop.presentation.game.ui.bottompanel.getIconResId
import com.example.dicedrop.presentation.ui.theme.DarkBlue

@Composable
fun NewRecordContent(
    record: String,
    isLandscape: Boolean,
    rewardsList: List<BonusItem>,
) {
    val titleSize = if (isLandscape) 14.sp else 22.sp
    val recordSize = if (isLandscape) 32.sp else 50.sp

    Text(
        text = stringResource(R.string.score),
        color = Color.Black,
        fontSize = titleSize,
        fontWeight = FontWeight.Normal
    )

    Text(
        text = record,
        color = DarkBlue,
        fontSize = recordSize,
        fontWeight = FontWeight.Bold
    )

    Spacer(
        modifier = Modifier.height(8.dp)
    )

    Text(
        text = stringResource(R.string.rewards),
        color = Color.Black,
        fontSize = titleSize,
        fontWeight = FontWeight.Normal
    )

    Spacer(
        modifier = Modifier.height(8.dp)
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        for (rewardItem in rewardsList){
            IconWithCoinsCount(
                reward = rewardItem.count,
                icCoinsId = rewardItem.type.getIconResId()
            )
        }
    }

}
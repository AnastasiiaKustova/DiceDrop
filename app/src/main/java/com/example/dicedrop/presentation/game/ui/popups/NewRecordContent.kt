package com.example.dicedrop.presentation.game.ui.popups

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.domain.entity.BonusItem
import com.example.dicedrop.presentation.achievements.ui.IconWithCoinsCount
import com.example.dicedrop.presentation.game.ui.bottompanel.getIconResId
import com.example.dicedrop.presentation.ui.theme.DarkBlue

@Composable
fun NewRecordContent(
    record: String,
    rewardsList: List<BonusItem>,
) {
    Text(
        text = "Score",
        color = Color.Black,
        fontSize = 22.sp,
        fontWeight = FontWeight.Normal
    )

    Text(
        text = record,
        color = DarkBlue,
        fontSize = 50.sp,
        fontWeight = FontWeight.Bold
    )

    Spacer(
        modifier = Modifier.fillMaxHeight(0.1f)
    )

    Text(
        text = "Rewards",
        color = Color.Black,
        fontSize = 22.sp,
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
package com.example.dicedrop.presentation.game.ui.popups

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
import com.example.dicedrop.presentation.common.DialogScrim
import com.example.dicedrop.presentation.common.DieFace
import com.example.dicedrop.presentation.common.GameButton
import com.example.dicedrop.presentation.common.IconCircle
import com.example.dicedrop.presentation.common.consumeClicks
import com.example.dicedrop.presentation.ui.theme.DarkBlue
import com.example.dicedrop.presentation.ui.theme.DeepBlue
import com.example.dicedrop.presentation.ui.theme.MidBlue
import com.example.dicedrop.presentation.ui.theme.White

// Шторка "Закажи грань": игрок выбирает грань, а затем на поле - кубик, которому её поставить
@Composable
fun LuckyChoiceDialog(
    onPick: (Int) -> Unit,
    onCancel: () -> Unit
) {
    DialogScrim(
        contentAlignment = Alignment.BottomCenter,
        onDismiss = onCancel
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(20.dp, RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .background(DarkBlue, RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .consumeClicks()
                .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp, 5.dp)
                    .background(DeepBlue, RoundedCornerShape(3.dp))
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconCircle(iconRes = R.drawable.bonus_lucky_b_flat, size = 44.dp, iconSize = 32.dp)
                Text(
                    text = stringResource(R.string.lucky_dialog_title),
                    color = White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Text(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .alpha(0.85f),
                text = stringResource(R.string.lucky_dialog_hint),
                color = White,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                listOf(listOf(1, 2, 3), listOf(4, 5, 6)).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(22.dp)) {
                        row.forEach { face ->
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(MidBlue)
                                    .clickable { onPick(face) },
                                contentAlignment = Alignment.Center
                            ) {
                                DieFace(value = face, size = 56.dp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            GameButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.cancel),
                onClick = onCancel,
                color = MidBlue,
                contentColor = White,
                height = 50.dp,
                fontSize = 18.sp
            )
        }
    }
}

package com.example.dicedrop.presentation.game.ui.popups

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.presentation.common.DialogScrim
import com.example.dicedrop.presentation.common.GameButton
import com.example.dicedrop.presentation.common.GameIcons
import com.example.dicedrop.presentation.common.consumeClicks
import com.example.dicedrop.presentation.game.ui.bottompanel.getIconResId
import com.example.dicedrop.presentation.game.ui.bottompanel.getTitleResId
import com.example.dicedrop.presentation.ui.theme.DarkBlue
import com.example.dicedrop.presentation.ui.theme.DeepBlue
import com.example.dicedrop.presentation.ui.theme.MidBlue
import com.example.dicedrop.presentation.ui.theme.White

// Блок никуда не встаёт, но бонус его спасёт. Новичку окно объясняет, что делать, опытному
// игроку дальше хватает подсказки над лотком (StuckHint)
@Composable
fun StuckDialog(
    rescueBonuses: List<BonusType>,
    onUseBonusClick: () -> Unit,
    onRestartClick: () -> Unit
) {
    DialogScrim {
        Column(
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .fillMaxWidth()
                .background(DarkBlue, RoundedCornerShape(30.dp))
                .consumeClicks()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.stuck_title),
                color = White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                modifier = Modifier.alpha(0.85f),
                text = stringResource(R.string.stuck_text),
                color = White,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Только те бонусы, что спасают этот блок; на панели они же пульсируют
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                rescueBonuses.forEach { type ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(type.getIconResId()),
                            contentDescription = null,
                            modifier = Modifier
                                .size(64.dp)
                                .background(White, CircleShape)
                                .padding(10.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = stringResource(type.getTitleResId()),
                            color = White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            GameButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.stuck_use_bonus),
                onClick = onUseBonusClick
            )
            Spacer(modifier = Modifier.height(10.dp))
            GameButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.restart_game),
                onClick = onRestartClick,
                color = MidBlue,
                contentColor = White,
                icon = GameIcons.Restart
            )
        }
    }
}

// Короткая подсказка вместо окна, когда игрок уже видел окно несколько раз
@Composable
fun StuckHint(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier
            .background(DeepBlue, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        text = stringResource(R.string.stuck_hint),
        color = White,
        fontSize = 14.sp,
        fontWeight = FontWeight.ExtraBold,
        textAlign = TextAlign.Center
    )
}

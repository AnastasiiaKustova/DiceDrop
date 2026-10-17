package com.example.dicedrop.presentation.game.ui.popups

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
import com.example.dicedrop.presentation.ui.theme.DarkBlue
import com.example.dicedrop.presentation.ui.theme.TranslucentBlack
import com.example.dicedrop.presentation.ui.theme.White
import com.example.dicedrop.presentation.ui.theme.WhiteBlue

@Composable
fun LuckyChoiceDialog(onPick: (Int) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TranslucentBlack)
            .wrapContentSize(Alignment.Center)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Закажи грань",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Грань встанет сразу, тапни кубик",
                fontSize = 13.sp,
                color = WhiteBlue,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(listOf(1, 2, 3), listOf(4, 5, 6)).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp,
                            Alignment.CenterHorizontally)
                    ) {
                        row.forEach { face ->
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .background(White, RoundedCornerShape(14.dp))
                                    .border(3.dp, DarkBlue, RoundedCornerShape(14.dp))
                                    .clickable { onPick(face) },
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = face.toFaceRes()),
                                    contentDescription = null,
                                    modifier = Modifier.size(56.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun Int.toFaceRes(): Int = when (this) {
    1 -> R.drawable.dice_1
    2 -> R.drawable.dice_2
    3 -> R.drawable.dice_3
    4 -> R.drawable.dice_4
    5 -> R.drawable.dice_5
    else -> R.drawable.dice_6
}
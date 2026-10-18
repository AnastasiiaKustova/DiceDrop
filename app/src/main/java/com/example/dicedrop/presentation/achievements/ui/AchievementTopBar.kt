package com.example.dicedrop.presentation.achievements.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
import com.example.dicedrop.presentation.common.RoundImageButton
import com.example.dicedrop.presentation.ui.theme.White

@Composable
fun AchievementTopBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 20.dp)
    ) {
        RoundImageButton(
            modifier = Modifier.align(Alignment.CenterStart),
            iconRes = R.drawable.btn_back,
            contentDescription = stringResource(R.string.back),
            size = 44.dp,
            onClick = onBackClick
        )
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = stringResource(R.string.achievements),
            color = White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Black
        )
    }
}

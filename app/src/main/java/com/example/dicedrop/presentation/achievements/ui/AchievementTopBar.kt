package com.example.dicedrop.presentation.achievements.ui

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.dicedrop.R
import com.example.dicedrop.presentation.ui.theme.BlueGradientDark
import com.example.dicedrop.presentation.ui.theme.BlueGradientLight
import com.example.dicedrop.presentation.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementTopBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
) {
    CenterAlignedTopAppBar(
        modifier = modifier
            .background(
                Brush.linearGradient(
                    colors = listOf(BlueGradientLight, BlueGradientDark),
                    start = Offset(0f, 0f),
                    end = Offset(0f, Float.POSITIVE_INFINITY)
                )
            ),
        title = {
            Text(
                text = stringResource(R.string.achievements),
                color = White
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors().copy(
            containerColor = Color.Transparent
        ),
        navigationIcon = {
            IconButton(onClick = { onBackClick() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = White
                )
            }
        }
    )
}
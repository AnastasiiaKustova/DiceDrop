package com.example.dicedrop.presentation.shop.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.dicedrop.R
import com.example.dicedrop.presentation.ui.theme.BlueGradientDark
import com.example.dicedrop.presentation.ui.theme.BlueGradientLight
import com.example.dicedrop.presentation.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopTopBar(
    coinsValue: Int,
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
        title = { Text(
            text = "Shop",
            color = White
        ) },
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
        },
        actions = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 12.dp)
            ) {
                Text(
                    text = coinsValue.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = White
                )

                Spacer(modifier = Modifier.width(4.dp))

                Icon(
                    painter = painterResource(R.drawable.coin),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    )
}
package com.example.dicedrop.presentation.game

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.presentation.ui.theme.White
import com.example.dicedrop.presentation.ui.theme.Yellow

@Composable
fun GameSnackbarHost(
    snackbarHostState: SnackbarHostState
) {
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier.padding(bottom = 80.dp)
    ) { snackbarData ->
        Snackbar(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(12.dp)),
            containerColor = Color.Transparent,
            contentColor = White,
            action = {
                snackbarData.visuals.actionLabel?.let {
                    TextButton(onClick = {
                        snackbarHostState.currentSnackbarData?.performAction()
                    }) {
                        Text(it, color = Yellow)
                    }
                }
            }
        ) {
            Text(snackbarData.visuals.message, fontSize = 16.sp)
        }
    }
}
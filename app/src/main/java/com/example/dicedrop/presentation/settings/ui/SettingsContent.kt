package com.example.dicedrop.presentation.settings.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.dicedrop.presentation.settings.OpenReason
import com.example.dicedrop.presentation.settings.SettingsComponent
import com.example.dicedrop.presentation.settings.SettingsStore

@Composable
fun SettingsContent(component: SettingsComponent) {
    val state by component.model.collectAsState()

    Box{
        when(val gameState = state.settingsState){
            SettingsStore.State.SettingsState.Error -> Error()
            SettingsStore.State.SettingsState.Initial -> Initial()
            is SettingsStore.State.SettingsState.Loaded -> {
                when (gameState.openReason){
                    OpenReason.GamePause -> {
                        DrawPause(
                            settings = gameState.settings,
                            onSettingsChange = { component.onSettingsChangeClick(it) },
                            onBackClick = { component.onBackClick() },
                            onRestartClick = { component.onRestartGameClick() },
                            onMenuClick = { component.onBackClick() },
                            onTutorialFinished = { component.onTutorialFinished() }
                        )
                    }
                    OpenReason.RegularSettings -> {
                        DrawSettings(
                            settings = gameState.settings,
                            onBackClick = { component.onBackClick() },
                            onRestartClick = { component.onRestartGameClick() },
                            onSettingsChange = { component.onSettingsChangeClick(it) },
                            onExitClick = { component.onExitClick() },
                            onTutorialFinished = { component.onTutorialFinished() }
                        )
                    }
                }
            }
            SettingsStore.State.SettingsState.Loading -> Loading()
        }
    }
}

@Composable
private fun Initial() {

}

@Composable
private fun Error() {

}

@Composable
private fun Loading() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            color = MaterialTheme.colorScheme.background
        )
    }
}
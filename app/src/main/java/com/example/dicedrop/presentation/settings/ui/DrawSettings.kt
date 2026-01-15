package com.example.dicedrop.presentation.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
import com.example.dicedrop.domain.entity.GameSettings
import com.example.dicedrop.domain.entity.SettingTypes
import com.example.dicedrop.presentation.ui.theme.DarkBlue
import com.example.dicedrop.presentation.ui.theme.White
import com.example.dicedrop.presentation.ui.theme.WhiteBlue

@Composable
fun DrawSettings(
    modifier: Modifier = Modifier,
    settings: GameSettings,
    onBackClick: () -> Unit,
    onExitClick: () -> Unit,
    onRestartClick: () -> Unit,
    onSettingsChange: (settingType: SettingTypes) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp, vertical = 40.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                Modifier
                    .fillMaxWidth()
            ) {

                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = stringResource(R.string.settings_title),
                    fontWeight = FontWeight.Bold,
                    color = DarkBlue,
                    fontSize = 28.sp
                )

                IconButton(
                    onClick = {
                        onBackClick()
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = (stringResource(R.string.back)),
                        modifier = Modifier
                            .background(WhiteBlue)
                            .size(SwitchDefaults.IconSize * 2),
                        tint = DarkBlue
                    )
                }
            }

            Spacer(
                Modifier.height(20.dp)
            )

            SettingsItem(
                icon = Icons.Filled.Check,
                title = stringResource(R.string.music_volume),
                type = SettingTypes.MusicVolume(settings.musicVolume),
                onSettingsChange = onSettingsChange
            )

            SettingsItem(
                icon = Icons.Filled.Check,
                title = stringResource(R.string.auto_skip),
                type = SettingTypes.UseAutoRoll(settings.useAutoRoll),
                onSettingsChange = onSettingsChange
            )



            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                onClick = {
                    onRestartClick()
                },
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    contentColor = DarkBlue,
                    containerColor = White
                )
            ) {
                Text(
                    text = stringResource(R.string.restart_game)
                )
            }

            OutlinedButton(
                onClick = {
                    onExitClick()
                },
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    contentColor = DarkBlue,
                    containerColor = White
                )
            ) {
                Text(
                    text = stringResource(R.string.exit)
                )
            }
        }
    }
}
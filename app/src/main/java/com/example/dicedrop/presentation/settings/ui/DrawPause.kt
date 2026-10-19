package com.example.dicedrop.presentation.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
import com.example.dicedrop.domain.entity.settings.GameSettings
import com.example.dicedrop.domain.entity.settings.SettingTypes
import com.example.dicedrop.presentation.common.DialogScrim
import com.example.dicedrop.presentation.common.GameButton
import com.example.dicedrop.presentation.common.GameIcons
import com.example.dicedrop.presentation.common.TutorialDialog
import com.example.dicedrop.presentation.common.consumeClicks
import com.example.dicedrop.presentation.common.isLandscape
import com.example.dicedrop.presentation.ui.theme.DarkBlue
import com.example.dicedrop.presentation.ui.theme.MidBlue
import com.example.dicedrop.presentation.ui.theme.White

@Composable
fun DrawPause(
    modifier: Modifier = Modifier,
    settings: GameSettings,
    onSettingsChange: (SettingTypes) -> Unit,
    onBackClick: () -> Unit,
    onRestartClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    // Обучение открывается вместо окна паузы и возвращает к нему по закрытии
    var showTutorial by rememberSaveable { mutableStateOf(false) }
    if (showTutorial) {
        TutorialDialog(onClose = { showTutorial = false })
        return
    }

    // Пауза лежит поверх игры: касания к полю под ней не проходят
    DialogScrim {
        Column(
            modifier = modifier
                .padding(horizontal = 40.dp)
                .fillMaxWidth()
                .background(DarkBlue, RoundedCornerShape(30.dp))
                .consumeClicks()
                .padding(start = 20.dp, end = 20.dp, top = 22.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.pause_title),
                color = White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            GameButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.continue_game),
                onClick = onBackClick,
                height = 60.dp,
                corner = 20.dp,
                fontSize = 22.sp,
                icon = GameIcons.Play
            )

            if (isLandscape()) {
                // На планшете окно ниже по высоте: «Как играть» и «Заново» стоят рядом
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GameButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.how_to_play),
                        onClick = { showTutorial = true },
                        color = MidBlue,
                        contentColor = White,
                        icon = GameIcons.Help,
                        fontSize = 17.sp
                    )
                    GameButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.restart_short),
                        onClick = onRestartClick,
                        color = MidBlue,
                        contentColor = White,
                        icon = GameIcons.Restart,
                        fontSize = 17.sp
                    )
                }

                GameButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.to_menu),
                    onClick = onMenuClick,
                    color = MidBlue,
                    contentColor = White
                )
            } else {
                GameButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.restart_game),
                    onClick = onRestartClick,
                    color = MidBlue,
                    contentColor = White
                )

                GameButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.to_menu),
                    onClick = onMenuClick,
                    color = MidBlue,
                    contentColor = White
                )

                GameButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.how_to_play),
                    onClick = { showTutorial = true },
                    color = MidBlue,
                    contentColor = White,
                    icon = GameIcons.Help
                )
            }

            // Те же переключатели, что в настройках, чтобы не выходить ради них в меню
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RoundToggle(
                    icon = GameIcons.Sound,
                    title = stringResource(R.string.sound),
                    checked = settings.isSoundOn,
                    onCheckedChange = { onSettingsChange(SettingTypes.Sound(it)) }
                )
                RoundToggle(
                    icon = GameIcons.Music,
                    title = stringResource(R.string.music),
                    checked = settings.isMusicOn,
                    onCheckedChange = { onSettingsChange(SettingTypes.Music(it)) }
                )
                RoundToggle(
                    icon = GameIcons.Vibration,
                    title = stringResource(R.string.vibration),
                    checked = settings.isVibrationOn,
                    onCheckedChange = { onSettingsChange(SettingTypes.Vibration(it)) }
                )
            }
        }
    }
}

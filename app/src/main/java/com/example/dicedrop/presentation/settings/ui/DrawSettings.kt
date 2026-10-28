package com.example.dicedrop.presentation.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
import com.example.dicedrop.domain.feedback.GameFeedback
import com.example.dicedrop.presentation.common.LocalGameFeedback
import com.example.dicedrop.domain.entity.settings.GameSettings
import com.example.dicedrop.domain.entity.settings.SettingTypes
import com.example.dicedrop.presentation.common.DialogScrim
import com.example.dicedrop.presentation.common.GameButton
import com.example.dicedrop.presentation.common.GameIcons
import com.example.dicedrop.presentation.common.TutorialDialog
import com.example.dicedrop.presentation.common.consumeClicks
import com.example.dicedrop.presentation.ui.theme.DarkBlue
import com.example.dicedrop.presentation.ui.theme.DeepBlue
import com.example.dicedrop.presentation.ui.theme.MidBlue
import com.example.dicedrop.presentation.ui.theme.White

@Composable
fun DrawSettings(
    modifier: Modifier = Modifier,
    settings: GameSettings,
    onBackClick: () -> Unit,
    onExitClick: () -> Unit,
    onRestartClick: () -> Unit,
    onSettingsChange: (settingType: SettingTypes) -> Unit,
    onTutorialFinished: () -> Unit = {}
) {
    val feedback = LocalGameFeedback.current

    // Обучение открывается вместо окна настроек и возвращает к нему по закрытии
    var showTutorial by rememberSaveable { mutableStateOf(false) }
    if (showTutorial) {
        TutorialDialog(
            onClose = { showTutorial = false },
            onFinish = {
                showTutorial = false
                onTutorialFinished()
            }
        )
        return
    }

    DialogScrim(onDismiss = onBackClick) {
        Column(
            modifier = modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .background(DarkBlue, RoundedCornerShape(30.dp))
                .consumeClicks()
                .padding(start = 18.dp, end = 18.dp, top = 18.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = stringResource(R.string.settings_title),
                    color = White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(DeepBlue)
                        .clickable {
                            feedback.play(GameFeedback.Sound.BUTTON_CLICK)
                            onBackClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = GameIcons.Close,
                        contentDescription = stringResource(R.string.close),
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ToggleSetting(
                    icon = GameIcons.Sound,
                    title = stringResource(R.string.sound),
                    checked = settings.isSoundOn,
                    onCheckedChange = { onSettingsChange(SettingTypes.Sound(it)) }
                )
                ToggleSetting(
                    icon = GameIcons.Music,
                    title = stringResource(R.string.music),
                    checked = settings.isMusicOn,
                    onCheckedChange = { onSettingsChange(SettingTypes.Music(it)) }
                )
                ToggleSetting(
                    icon = GameIcons.Vibration,
                    title = stringResource(R.string.vibration),
                    checked = settings.isVibrationOn,
                    onCheckedChange = { onSettingsChange(SettingTypes.Vibration(it)) }
                )
            }

            GameButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.how_to_play),
                onClick = { showTutorial = true },
                color = MidBlue,
                contentColor = White,
                icon = GameIcons.Help
            )

            GameButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.restart_game),
                onClick = onRestartClick,
                color = MidBlue,
                contentColor = White
            )

            GameButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.exit),
                onClick = onExitClick,
                color = DeepBlue,
                contentColor = White,
                icon = GameIcons.Exit
            )
        }
    }
}

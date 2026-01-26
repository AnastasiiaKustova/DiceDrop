package com.example.dicedrop.presentation.settings.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.domain.entity.settings.SettingTypes
import com.example.dicedrop.presentation.ui.theme.DarkBlue
import com.example.dicedrop.presentation.ui.theme.WhiteBlue

@Composable
fun SettingsItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    type: SettingTypes,
    onSettingsChange: (settingsType: SettingTypes) -> Unit
){
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = DarkBlue,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        when (type){
            is SettingTypes.UseAutoRoll -> {
                SettingSwitch(
                    value = type.checked,
                    onSettingsChange = {
                        onSettingsChange(
                            SettingTypes.UseAutoRoll(it)
                        )
                    }
                )
            }
            is SettingTypes.MusicVolume -> {
                SettingSlider(
                    value = type.value,
                    onSettingsChange = {
                        onSettingsChange(
                            SettingTypes.MusicVolume(it)
                        )
                    }
                )
            }

            is SettingTypes.Language -> {}
        }
    }
}

@Composable
fun SettingSwitch(
    modifier: Modifier = Modifier,
    value: Boolean,
    onSettingsChange: (newValue: Boolean) -> Unit
){
    Switch(
        modifier = modifier,
        colors = SwitchDefaults.colors().copy(
            checkedTrackColor = DarkBlue,
            checkedIconColor = DarkBlue,
            uncheckedTrackColor = WhiteBlue,
            uncheckedBorderColor = DarkBlue,
            uncheckedThumbColor = DarkBlue
        ),
        checked = value,
        onCheckedChange = { onSettingsChange(it) },
        thumbContent = {
            if(value){
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(
                        SwitchDefaults.IconSize
                    )
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingSlider(
    modifier: Modifier = Modifier,
    value: Float,
    onSettingsChange: (newValue: Float) -> Unit
){
    var sliderPosition by rememberSaveable { mutableStateOf(value) }
    val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
    Column(modifier = Modifier.padding(start = 16.dp)) {
        Slider(
            modifier = modifier
                .height(50.dp),
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            valueRange = 0f..100f,
            interactionSource = interactionSource,
            onValueChangeFinished = {
                onSettingsChange(sliderPosition)
            },
            colors = SliderDefaults.colors().copy(
                thumbColor = DarkBlue
            ),
            track = { sliderState ->
                SliderDefaults.Track(
                    modifier = Modifier.scale(scaleX = 1f, scaleY = 2.5f),
                    sliderState = sliderState,
                    colors = SliderDefaults.colors().copy(
                        activeTrackColor = DarkBlue,
                        inactiveTrackColor = WhiteBlue
                    )
                )
            },
        )
    }
}

@Composable
fun SettingDropdownMenu(
    modifier: Modifier = Modifier,
    value: Boolean,
    onSettingsChange: (settingsType: SettingTypes) -> Unit
){

}
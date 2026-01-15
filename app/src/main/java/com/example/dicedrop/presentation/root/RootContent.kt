package com.example.dicedrop.presentation.root

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.example.dicedrop.presentation.achievements.AchievementsContent
import com.example.dicedrop.presentation.game.ui.GameContent
import com.example.dicedrop.presentation.menu.MenuContent
import com.example.dicedrop.presentation.settings.ui.SettingsContent
import com.example.dicedrop.presentation.shop.ShopContent
import com.example.dicedrop.presentation.ui.theme.DiceDropTheme

@Composable
fun RootContent(component: RootComponent) {
    DiceDropTheme{
        Box(modifier = Modifier.fillMaxSize()){
            Children(stack = component.stack) {
                when (val instance = it.instance){
                    is RootComponent.Child.Game -> GameContent(component = instance.component)
                    is RootComponent.Child.Menu -> MenuContent(component = instance.component)
                    is RootComponent.Child.Achievements -> AchievementsContent(component = instance.component)
                    is RootComponent.Child.Settings -> SettingsContent(component = instance.component)
                    is RootComponent.Child.Shop -> ShopContent(component = instance.component)
                }
            }
        }
    }
}
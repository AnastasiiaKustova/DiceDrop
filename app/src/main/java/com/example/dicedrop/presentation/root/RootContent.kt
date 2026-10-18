package com.example.dicedrop.presentation.root

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.example.dicedrop.presentation.achievements.AchievementsContent
import com.example.dicedrop.presentation.game.ui.GameContent
import com.example.dicedrop.presentation.menu.MenuContent
import com.example.dicedrop.presentation.settings.ui.SettingsContent
import com.example.dicedrop.presentation.shop.ShopContent
import com.example.dicedrop.presentation.ui.theme.DiceDropTheme

// Макеты нарисованы под экран 390x844
private val DesignWidth = 390.dp
private val DesignHeight = 844.dp

@Composable
fun RootContent(component: RootComponent) {
    DiceDropTheme{
        BoxWithConstraints(modifier = Modifier.fillMaxSize()){
            // На экранах меньше макета весь интерфейс пропорционально уменьшается,
            // а системное увеличение шрифта не ломает вёрстку
            val density = LocalDensity.current
            val scale = minOf(1f, maxWidth / DesignWidth, maxHeight / DesignHeight)
            CompositionLocalProvider(
                LocalDensity provides Density(density = density.density * scale, fontScale = 1f)
            ) {
                Screens(component)
            }
        }
    }
}

@Composable
private fun Screens(component: RootComponent) {
    Box(modifier = Modifier.fillMaxSize()){
        val stack by component.stack.subscribeAsState()

        // Настройки - окно поверх меню, поэтому меню под ним остаётся видно
        if (stack.active.instance is RootComponent.Child.Settings) {
            (stack.backStack.lastOrNull()?.instance as? RootComponent.Child.Menu)?.let {
                MenuContent(component = it.component)
            }
        }

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

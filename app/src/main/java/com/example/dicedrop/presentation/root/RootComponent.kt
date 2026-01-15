package com.example.dicedrop.presentation.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.example.dicedrop.presentation.achievements.AchievementsComponent
import com.example.dicedrop.presentation.game.GameComponent
import com.example.dicedrop.presentation.menu.MenuComponent
import com.example.dicedrop.presentation.settings.SettingsComponent
import com.example.dicedrop.presentation.shop.ShopComponent

interface RootComponent {

    val stack : Value<ChildStack<*, Child>>

    sealed interface Child{
        class Menu(val component: MenuComponent): Child
        class Game(val component: GameComponent): Child
        class Shop(val component: ShopComponent): Child
        class Achievements(val component: AchievementsComponent): Child
        class Settings(val component: SettingsComponent): Child
    }
}
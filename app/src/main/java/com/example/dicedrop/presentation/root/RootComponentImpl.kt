package com.example.dicedrop.presentation.root

import android.os.Parcelable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.Value
import com.example.dicedrop.core.componentScope
import com.example.dicedrop.domain.engine.AchievementTracker
import com.example.dicedrop.presentation.achievements.AchievementsComponentImpl
import com.example.dicedrop.presentation.game.GameComponentImpl
import com.example.dicedrop.presentation.menu.MenuComponentImpl
import com.example.dicedrop.presentation.settings.OpenReason
import com.example.dicedrop.presentation.settings.SettingsComponentImpl
import com.example.dicedrop.presentation.shop.ShopComponentImpl
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.launch

class RootComponentImpl @AssistedInject constructor(
    private val gameComponentImplFactory: GameComponentImpl.Factory,
    private val menuComponentImplFactory: MenuComponentImpl.Factory,
    private val achievementsComponentImplFactory: AchievementsComponentImpl.Factory,
    private val shopComponentImplFactory: ShopComponentImpl.Factory,
    private val settingsComponentImplFactory: SettingsComponentImpl.Factory,
    private val achievementTracker: AchievementTracker,
    @Assisted("onExitApp") private val onExitApp: () -> Unit,
    @Assisted("componentContext") componentContext: ComponentContext
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    private val scope = componentScope()

    override val stack: Value<ChildStack<*, RootComponent.Child>> = childStack(
        source = navigation,
        initialConfiguration = Config.Menu,
        handleBackButton = true,
        childFactory = ::child
    )

    private fun child(
        config: Config,
        componentContext: ComponentContext
    ): RootComponent.Child {
        return when (config) {
            Config.Game -> {
                val component = gameComponentImplFactory.create(
                    navigateBackClicked = {
                        navigation.pop()
                    },
                    navigateToShopClicked = {
                        navigation.push(Config.Shop)
                    },
                    navigateToAchievementsClicked = {
                        navigation.push(Config.Achievements)
                    },
                    componentContext = componentContext
                )
                RootComponent.Child.Game(component)
            }

            Config.Menu -> {
                val component = menuComponentImplFactory.create(
                    onStartGameClicked = {
                        navigation.push(Config.Game)
                    },
                    onAchievementsClicked = {
                        navigation.push(Config.Achievements)
                    },
                    onShopClicked = {
                        navigation.push(Config.Shop)
                    },
                    onSettingsClicked = {
                        navigation.push(Config.Settings(OpenReason.RegularSettings))
                    },
                    componentContext = componentContext
                )
                RootComponent.Child.Menu(component)
            }

            is Config.Settings -> {
                if (config.openReason == OpenReason.RegularSettings) visited(AchievementTracker.Section.SETTINGS)
                val component = settingsComponentImplFactory.create(
                    openReason = config.openReason,
                    onExitClicked = {
                        onExitApp()
                    },
                    onBackClicked = {
                        navigation.pop()
                    },
                    onRestartGameClicked = {
                        navigation.pop()
                    },
                    onNavigateToShopClicked = {
                        navigation.push(Config.Shop)
                    },
                    componentContext = componentContext
                )
                RootComponent.Child.Settings(component)
            }

            Config.Achievements -> {
                visited(AchievementTracker.Section.ACHIEVEMENTS)
                val component = achievementsComponentImplFactory.create(
                    onBackClicked = {
                        navigation.pop()
                    },
                    componentContext = componentContext
                )
                RootComponent.Child.Achievements(component)
            }

            Config.Shop -> {
                visited(AchievementTracker.Section.SHOP)
                val component = shopComponentImplFactory.create(
                    onBackClicked = {
                        navigation.pop()
                    },
                    componentContext = componentContext
                )
                RootComponent.Child.Shop(component)
            }
        }
    }

    // «Следопыт»: открыты магазин, достижения и настройки
    private fun visited(section: AchievementTracker.Section) {
        scope.launch { achievementTracker.onSectionVisited(section) }
    }

    private sealed interface Config : Parcelable {
        @Parcelize
        data object Game : Config

        @Parcelize
        data object Menu : Config

        @Parcelize
        data class Settings(val openReason: OpenReason) : Config

        @Parcelize
        data object Achievements : Config

        @Parcelize
        data object Shop : Config
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("onExitApp") onExitApp: () -> Unit,
            @Assisted("componentContext") componentContext: ComponentContext,
        ): RootComponentImpl
    }
}
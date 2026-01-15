package com.example.dicedrop.presentation.menu

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.example.dicedrop.core.componentScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch

class MenuComponentImpl @AssistedInject constructor(
    private val storeFactory: MenuStoreFactory,
    @Assisted("onStartGameClicked") private val onStartGameClicked: () -> Unit,
    @Assisted("onAchievementsClicked") private val onAchievementsClicked: () -> Unit,
    @Assisted("onShopClicked") private val onShopClicked: () -> Unit,
    @Assisted("onCloseAppClicked") private val onCloseAppClicked: () -> Unit,
    @Assisted("componentContext") componentContext: ComponentContext,
) : MenuComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore { storeFactory.create() }
    private val scope = componentScope()

    init {
        scope.launch {
            store.labels.collect{
                when(it){
                    MenuStore.Label.ClickCloseApp -> onCloseAppClicked()
                    MenuStore.Label.ClickNavigateToAchievements ->  onAchievementsClicked()
                    MenuStore.Label.ClickNavigateToGame -> onStartGameClicked()
                    MenuStore.Label.ClickNavigateToShop -> onShopClicked()
                }
            }
        }
    }


    override fun onStartGameClick() {
        store.accept(MenuStore.Intent.ClickNavigateToGame)
    }

    override fun onAchievementsClick() {
        store.accept(MenuStore.Intent.ClickNavigateToAchievements)
    }

    override fun onShopClick() {
        store.accept(MenuStore.Intent.ClickNavigateToShop)
    }

    override fun onCloseAppClick() {
        store.accept(MenuStore.Intent.ClickCloseApp)
    }

    @AssistedFactory
    interface Factory{
        fun create(
            @Assisted("onStartGameClicked") onStartGameClicked: () -> Unit,
            @Assisted("onAchievementsClicked") onAchievementsClicked: () -> Unit,
            @Assisted("onShopClicked") onShopClicked: () -> Unit,
            @Assisted("onCloseAppClicked") onCloseAppClicked: () -> Unit,
            @Assisted("componentContext") componentContext: ComponentContext,
        ): MenuComponentImpl
    }
}
package com.example.dicedrop.presentation.menu

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnResume
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.dicedrop.core.componentScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MenuComponentImpl @AssistedInject constructor(
    private val storeFactory: MenuStoreFactory,
    @Assisted("onStartGameClicked") private val onStartGameClicked: () -> Unit,
    @Assisted("onAchievementsClicked") private val onAchievementsClicked: () -> Unit,
    @Assisted("onShopClicked") private val onShopClicked: () -> Unit,
    @Assisted("onSettingsClicked") private val onSettingsClicked: () -> Unit,
    @Assisted("componentContext") componentContext: ComponentContext,
) : MenuComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore { storeFactory.create() }
    private val scope = componentScope()

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<MenuStore.State> = store.stateFlow

    private val _uiEvents = MutableSharedFlow<MenuComponent.UiEvent>(extraBufferCapacity = 4)
    override val uiEvents: SharedFlow<MenuComponent.UiEvent> = _uiEvents

    init {
        scope.launch {
            store.labels.collect{
                when(it){
                    MenuStore.Label.ClickNavigateToAchievements ->  onAchievementsClicked()
                    MenuStore.Label.ClickNavigateToGame -> onStartGameClicked()
                    MenuStore.Label.ClickNavigateToShop -> onShopClicked()
                    MenuStore.Label.ClickNavigateToSettings -> onSettingsClicked()
                    MenuStore.Label.GiftAlreadyReceived -> _uiEvents.emit(MenuComponent.UiEvent.GiftAlreadyReceived)
                }
            }
        }

        lifecycle.doOnResume {
            store.accept(MenuStore.Intent.Refresh)
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

    override fun onSettingsClick() {
        store.accept(MenuStore.Intent.ClickNavigateToSettings)
    }

    override fun onGiftClick() {
        store.accept(MenuStore.Intent.ClickGift)
    }

    override fun onGiftConfirm() {
        store.accept(MenuStore.Intent.GiftConfirm)
    }

    override fun onTutorialClose() {
        store.accept(MenuStore.Intent.TutorialClose)
    }

    override fun onTutorialFinish() {
        store.accept(MenuStore.Intent.TutorialFinish)
    }

    @AssistedFactory
    interface Factory{
        fun create(
            @Assisted("onStartGameClicked") onStartGameClicked: () -> Unit,
            @Assisted("onAchievementsClicked") onAchievementsClicked: () -> Unit,
            @Assisted("onShopClicked") onShopClicked: () -> Unit,
            @Assisted("onSettingsClicked") onSettingsClicked: () -> Unit,
            @Assisted("componentContext") componentContext: ComponentContext,
        ): MenuComponentImpl
    }
}

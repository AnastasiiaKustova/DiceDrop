package com.example.dicedrop.presentation.settings

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.dicedrop.core.componentScope
import com.example.dicedrop.domain.entity.SettingTypes
import com.example.dicedrop.presentation.settings.SettingsComponent.UiEvent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsComponentImpl @AssistedInject constructor(
    private val storeFactory: SettingsStoreFactory,
    @Assisted("openReason") private val openReason: OpenReason,
    @Assisted("onBackClicked") private val onBackClicked: () -> Unit,
    @Assisted("onExitClicked") private val onExitClicked: () -> Unit,
    @Assisted("onNavigateToShopClicked") private val onNavigateToShopClicked: () -> Unit,
    @Assisted("onRestartGameClicked") private val onRestartGameClicked: () -> Unit,
    @Assisted("componentContext") componentContext: ComponentContext,
) : SettingsComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore { storeFactory.create(openReason) }
    private val scope = componentScope()

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    override val uiEvents: SharedFlow<UiEvent> = _uiEvents

    init {
        scope.launch {
            store.labels.collect {
                when (it) {
                    SettingsStore.Label.ClickBack -> onBackClicked()
                    SettingsStore.Label.ClickNavigateToShop -> onNavigateToShopClicked()
                    SettingsStore.Label.ClickRestartGame -> onRestartGameClicked()
                    is SettingsStore.Label.ClickChangeLanguageError -> _uiEvents.emit(UiEvent.ShowError(UiEvent.ErrorType.ChangeLanguageError))
                    is SettingsStore.Label.ClickTurnOffAds -> _uiEvents.emit(UiEvent.ShowMessage(UiEvent.MessageType.AdsOffSuccess))
                    SettingsStore.Label.ClickExit -> onExitClicked()
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<SettingsStore.State> = store.stateFlow

    override fun onBackClick() {
        store.accept(SettingsStore.Intent.ClickBack)
    }

    override fun onExitClick() {
        store.accept(SettingsStore.Intent.ClickExit)
    }

    override fun onNavigateToShopClick() {
        store.accept(SettingsStore.Intent.ClickNavigateToShop)
    }

    override fun onRestartGameClick() {
        store.accept(SettingsStore.Intent.ClickRestartGame)
    }

    override fun onSettingsChangeClick(settingType: SettingTypes) {
        store.accept(SettingsStore.Intent.ClickSettingsChange(settingType))
    }


    override fun onTurnOffAds() {
        store.accept(SettingsStore.Intent.ClickTurnOffAds)
    }

    @AssistedFactory
    interface Factory{
        fun create(
            @Assisted("onBackClicked") onBackClicked: () -> Unit,
            @Assisted("onExitClicked") onExitClicked: () -> Unit,
            @Assisted("openReason") openReason: OpenReason,
            @Assisted("onNavigateToShopClicked") onNavigateToShopClicked: () -> Unit,
            @Assisted("onRestartGameClicked") onRestartGameClicked: () -> Unit,
            @Assisted("componentContext") componentContext: ComponentContext,
        ) : SettingsComponentImpl
    }
}
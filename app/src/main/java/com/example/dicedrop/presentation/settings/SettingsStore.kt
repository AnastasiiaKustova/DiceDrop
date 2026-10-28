package com.example.dicedrop.presentation.settings

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.example.dicedrop.domain.engine.AchievementTracker
import com.example.dicedrop.domain.entity.statistic.PlayerStat
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.dicedrop.domain.entity.settings.GameSettings
import com.example.dicedrop.domain.entity.settings.SettingTypes
import com.example.dicedrop.domain.useCases.game.RestartGameUseCase
import com.example.dicedrop.domain.useCases.settings.ChangeLanguageUseCase
import com.example.dicedrop.domain.useCases.settings.ChangeMusicUseCase
import com.example.dicedrop.domain.useCases.settings.ChangeSoundUseCase
import com.example.dicedrop.domain.useCases.settings.ChangeVibrationUseCase
import com.example.dicedrop.domain.useCases.settings.GetSettingsUseCase
import com.example.dicedrop.presentation.settings.SettingsStore.Intent
import com.example.dicedrop.presentation.settings.SettingsStore.Label
import com.example.dicedrop.presentation.settings.SettingsStore.State
import kotlinx.coroutines.launch
import javax.inject.Inject

interface SettingsStore: Store<Intent, State, Label> {

    sealed interface Intent {

        data object ClickBack : Intent

        data object ClickExit : Intent

        data object ClickRestartGame : Intent

        data object ClickNavigateToShop : Intent

        data class ClickSettingsChange(val newSetting: SettingTypes) : Intent

        data object TutorialFinished : Intent

    }

    data class State(
        val settingsState: SettingsState
    ){
        sealed interface SettingsState {

            data object Initial : SettingsState

            data object Loading : SettingsState

            data object Error : SettingsState

            data class Loaded(val settings: GameSettings, val openReason: OpenReason) : SettingsState
        }
    }

    sealed interface Label {

        data object ClickBack : Label

        data object ClickExit : Label

        data object ClickRestartGame : Label

        data object ClickNavigateToShop : Label

        data class ClickChangeLanguageError(val language: String) : Label
    }
}

class SettingsStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val changeLanguageUseCase: ChangeLanguageUseCase,
    private val changeSoundUseCase: ChangeSoundUseCase,
    private val changeVibrationUseCase: ChangeVibrationUseCase,
    private val changeMusicUseCase: ChangeMusicUseCase,
    private val restartGameUseCase: RestartGameUseCase,
    private val achievementTracker: AchievementTracker,
) {

    fun create(openReason: OpenReason): SettingsStore =
        object : SettingsStore, Store<Intent, State, Label> by storeFactory.create(
            name = "SettingsStore",
            initialState = State(
                settingsState = State.SettingsState.Initial
            ),
            bootstrapper = BootstrapperImpl(openReason),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {

        data class SettingsLoaded(val settings: GameSettings, val openReason: OpenReason) : Action

        data object SettingsStartLoading : Action

        data object SettingsLoadingError : Action

    }

    private sealed interface Msg {

        data class LanguageChanged(val language: String): Msg

        data class SoundChanged(val isOn: Boolean): Msg

        data class VibrationChanged(val isOn: Boolean): Msg

        data class MusicChanged(val isOn: Boolean): Msg

        data class SettingsLoaded(val settings: GameSettings, val openReason: OpenReason) : Msg

        data object SettingsStartLoading : Msg

        data object SettingsLoadingError : Msg

    }

    private inner class BootstrapperImpl(private val openReason: OpenReason): CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                dispatch(Action.SettingsStartLoading)
                try {
                    val settings = getSettingsUseCase()
                    dispatch(Action.SettingsLoaded(settings, openReason))
                } catch (e: Exception) {
                    dispatch(Action.SettingsLoadingError)
                }
            }
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                Intent.ClickBack -> publish(Label.ClickBack)
                Intent.ClickNavigateToShop -> publish(Label.ClickNavigateToShop)
                Intent.ClickRestartGame -> {
                    scope.launch {
                        restartGameUseCase()
                        publish(Label.ClickRestartGame)
                    }
                }
                Intent.ClickExit -> publish(Label.ClickExit)
                Intent.TutorialFinished -> scope.launch { achievementTracker.mark(PlayerStat.TUTORIAL_COMPLETED) }
                is Intent.ClickSettingsChange -> {
                    scope.launch {
                        when(intent.newSetting){
                            is SettingTypes.Language -> {
                                try {
                                    changeLanguageUseCase(intent.newSetting.language)
                                    dispatch(Msg.LanguageChanged(intent.newSetting.language))
                                } catch (e: Exception) {
                                    publish(Label.ClickChangeLanguageError(intent.newSetting.language))
                                }
                            }
                            is SettingTypes.Sound -> {
                                // Переключатель меняется сразу, сохранение идёт следом
                                dispatch(Msg.SoundChanged(intent.newSetting.isOn))
                                changeSoundUseCase(intent.newSetting.isOn)
                            }
                            is SettingTypes.Vibration -> {
                                dispatch(Msg.VibrationChanged(intent.newSetting.isOn))
                                changeVibrationUseCase(intent.newSetting.isOn)
                            }
                            is SettingTypes.Music -> {
                                dispatch(Msg.MusicChanged(intent.newSetting.isOn))
                                changeMusicUseCase(intent.newSetting.isOn)
                            }
                        }
                    }
                }
            }
        }

        override fun executeAction(action: Action, getState: () -> State) {
            when (action) {
                is Action.SettingsLoaded -> dispatch(Msg.SettingsLoaded(action.settings, action.openReason))
                Action.SettingsLoadingError -> dispatch(Msg.SettingsLoadingError)
                Action.SettingsStartLoading -> dispatch(Msg.SettingsStartLoading)
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State = when (msg) {
            is Msg.LanguageChanged -> updateSettings { it.copy(language = msg.language) }
            is Msg.SoundChanged -> updateSettings { it.copy(isSoundOn = msg.isOn) }
            is Msg.VibrationChanged -> updateSettings { it.copy(isVibrationOn = msg.isOn) }
            is Msg.MusicChanged -> updateSettings { it.copy(isMusicOn = msg.isOn) }
            Msg.SettingsLoadingError -> copy(settingsState = State.SettingsState.Error)
            Msg.SettingsStartLoading ->  copy(settingsState = State.SettingsState.Loading)
            is Msg.SettingsLoaded -> copy(
                settingsState = State.SettingsState.Loaded(msg.settings, msg.openReason)
            )
        }
    }
}

private fun State.updateSettings(transform: (GameSettings) -> GameSettings): State =
    (settingsState as? State.SettingsState.Loaded)?.let { state ->
        copy(settingsState = State.SettingsState.Loaded(transform(state.settings),state.openReason))
    } ?: this
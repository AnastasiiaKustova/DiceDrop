package com.example.dicedrop.presentation.achievements

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.dicedrop.core.IllegalException
import com.example.dicedrop.domain.entity.Achievement
import com.example.dicedrop.domain.useCases.achievements.GetAchievementRewardUseCase
import com.example.dicedrop.domain.useCases.achievements.GetAchievementsUseCase
import com.example.dicedrop.domain.useCases.achievements.ObserveAchievementStateUseCase
import javax.inject.Inject
import com.example.dicedrop.presentation.achievements.AchievementsStore.Intent
import com.example.dicedrop.presentation.achievements.AchievementsStore.Label
import com.example.dicedrop.presentation.achievements.AchievementsStore.State
import kotlinx.coroutines.launch

interface AchievementsStore: Store<Intent, State, Label> {

    sealed interface Intent {

        data object ClickBack : Intent

        data class ClickGetReward(val achievementId: String) : Intent
    }

    data class State(
        val achievementsState: AchievementsState
    ){
        sealed interface AchievementsState {
            data object Initial : AchievementsState

            data object Loading : AchievementsState

            data object Error : AchievementsState

            data class Loaded(val achievements: List<Achievement>) : AchievementsState
        }
    }

    sealed interface Label {
        data object ClickBack : Label

        data class AchievementRewardClaimed(val achievementId: String) : Label

        data class Error(val msg: String?) : Label
    }
}

class AchievementsStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val getAchievementsUseCase: GetAchievementsUseCase,
    private val getAchievementRewardUseCase: GetAchievementRewardUseCase,
    private val observeAchievementStateUseCase: ObserveAchievementStateUseCase
) {

    fun create(): AchievementsStore =
        object : AchievementsStore, Store<Intent, State, Label> by storeFactory.create(
            name = "AchievementsStore",
            initialState = State(
                achievementsState = State.AchievementsState.Initial
            ),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {

        data class AchievementsLoaded(val achievements: List<Achievement>) : Action

        data object AchievementsStartLoading : Action

        data object AchievementsLoadingError : Action
    }

    private sealed interface Msg {

        data class AchievementsLoaded(val achievements: List<Achievement>) : Msg

        data object AchievementsStartLoading : Msg

        data object AchievementsLoadingError : Msg
    }

    private inner class BootstrapperImpl: CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                dispatch(Action.AchievementsStartLoading)
                try {
                    val achievements = getAchievementsUseCase()
                    dispatch(Action.AchievementsLoaded(achievements))
                } catch (e: Exception) {
                    dispatch(Action.AchievementsLoadingError)
                }
            }
            scope.launch {
                observeAchievementStateUseCase().collect { progress ->
                    val achievements = getAchievementsUseCase()
                    dispatch(Action.AchievementsLoaded(achievements))
                }
            }
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                Intent.ClickBack -> {
                    publish(Label.ClickBack)
                }

                is Intent.ClickGetReward -> {
                    scope.launch {
                        try {
                            getAchievementRewardUseCase(intent.achievementId)

                            publish(
                                Label.AchievementRewardClaimed(intent.achievementId)
                            )
                        } catch (e: IllegalException){
                            publish(Label.Error(e.message))
                        }
                    }
                }


            }
        }

        override fun executeAction(action: Action, getState: () -> State) {
            when (action) {

                is Action.AchievementsLoaded -> {
                    dispatch(Msg.AchievementsLoaded(action.achievements))
                }

                Action.AchievementsLoadingError -> {
                    dispatch(Msg.AchievementsLoadingError)
                }

                Action.AchievementsStartLoading -> {
                    dispatch(Msg.AchievementsStartLoading)
                }

            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State = when (msg) {

            is Msg.AchievementsLoaded -> {
                copy(achievementsState = State.AchievementsState.Loaded(msg.achievements))
            }

            Msg.AchievementsLoadingError -> {
                copy(achievementsState = State.AchievementsState.Error)
            }

            Msg.AchievementsStartLoading -> {
                copy(achievementsState = State.AchievementsState.Loading)
            }
        }
    }
}
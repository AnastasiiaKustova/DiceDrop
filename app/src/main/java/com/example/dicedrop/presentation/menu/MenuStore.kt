package com.example.dicedrop.presentation.menu

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.dicedrop.presentation.menu.MenuStore.Intent
import com.example.dicedrop.presentation.menu.MenuStore.Label
import com.example.dicedrop.presentation.menu.MenuStore.State
import javax.inject.Inject

interface MenuStore : Store<Intent, State, Label> {

    sealed interface Intent {

        data object ClickNavigateToGame : Intent

        data object ClickNavigateToAchievements : Intent

        data object ClickNavigateToShop : Intent

        data object ClickCloseApp : Intent

    }

    data object State

    sealed interface Label {

        data object ClickNavigateToGame : Label

        data object ClickNavigateToAchievements : Label

        data object ClickNavigateToShop : Label

        data object ClickCloseApp : Label
    }
}

class MenuStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
) {

    fun create(): MenuStore =
        object : MenuStore, Store<Intent, State, Label> by storeFactory.create(
            name = "MenuStore",
            initialState = State,
            executorFactory = ::ExecutorImpl,
            reducer = NoOpReducer
        ) {}

    private object NoOpReducer : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State = this
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Nothing, State, Msg, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                Intent.ClickCloseApp -> publish(Label.ClickCloseApp)
                Intent.ClickNavigateToAchievements -> publish(Label.ClickNavigateToAchievements)
                Intent.ClickNavigateToGame -> publish(Label.ClickNavigateToGame)
                Intent.ClickNavigateToShop -> publish(Label.ClickNavigateToShop)
            }
        }
    }
}

private typealias Msg = Nothing
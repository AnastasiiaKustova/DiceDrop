package com.example.dicedrop.presentation.achievements

import com.arkivanov.decompose.ComponentContext
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

class AchievementsComponentImpl @AssistedInject constructor(
    private val storeFactory: AchievementsStoreFactory,
    @Assisted("onBackClicked") private val onBackClicked: () -> Unit,
    @Assisted("componentContext") componentContext: ComponentContext,
): AchievementsComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore { storeFactory.create() }
    private val scope = componentScope()

    private val _uiEvents = MutableSharedFlow<AchievementsComponent.UiEvent>(replay = 1)
    override val uiEvents: SharedFlow<AchievementsComponent.UiEvent> = _uiEvents

    init {
        scope.launch {
            store.labels.collect{
                when(it){
                    AchievementsStore.Label.ClickBack -> onBackClicked()
                    is AchievementsStore.Label.Error -> {}
                    is AchievementsStore.Label.AchievementRewardClaimed -> {
                        _uiEvents.emit(AchievementsComponent.UiEvent.AchievementRewardClaimed(it.achievementId))
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<AchievementsStore.State> = store.stateFlow

    override fun onBackClick() {
        store.accept(AchievementsStore.Intent.ClickBack)
    }

    override fun onGetRewardClick(achievementId: String) {
        store.accept(AchievementsStore.Intent.ClickGetReward(achievementId))
    }


    @AssistedFactory
    interface Factory{
        fun create(
            @Assisted("onBackClicked") onBackClicked: () -> Unit,
            @Assisted("componentContext") componentContext: ComponentContext,
        ) : AchievementsComponentImpl
    }
}
package com.example.dicedrop.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.example.dicedrop.data.feedback.BackgroundMusicImpl
import com.example.dicedrop.data.feedback.GameFeedbackImpl
import com.example.dicedrop.data.repository.AchievementRepositoryImpl
import com.example.dicedrop.data.repository.GameRepositoryImpl
import com.example.dicedrop.data.repository.PlayerStatsRepositoryImpl
import com.example.dicedrop.data.repository.SettingsRepositoryImpl
import com.example.dicedrop.data.repository.ShopRepositoryImpl
import com.example.dicedrop.domain.feedback.BackgroundMusic
import com.example.dicedrop.domain.feedback.GameFeedback
import com.example.dicedrop.domain.repository.AchievementRepository
import com.example.dicedrop.domain.repository.GameRepository
import com.example.dicedrop.domain.repository.PlayerStatsRepository
import com.example.dicedrop.domain.repository.SettingsRepository
import com.example.dicedrop.domain.repository.ShopRepository
import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface DataModule {

    @[ApplicationScope Binds]
    fun bindGameRepository(impl: GameRepositoryImpl): GameRepository

    @[ApplicationScope Binds]
    fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @[ApplicationScope Binds]
    fun bindShopRepository(impl: ShopRepositoryImpl): ShopRepository

    @[ApplicationScope Binds]
    fun bindAchievementRepository(impl: AchievementRepositoryImpl): AchievementRepository

    @[ApplicationScope Binds]
    fun bindPlayerStatsRepository(impl: PlayerStatsRepositoryImpl): PlayerStatsRepository

    @[ApplicationScope Binds]
    fun bindGameFeedback(impl: GameFeedbackImpl): GameFeedback

    @[ApplicationScope Binds]
    fun bindBackgroundMusic(impl: BackgroundMusicImpl): BackgroundMusic

    companion object{
        @[Provides ApplicationScope]
        fun provideDataStore(context: Context): DataStore<Preferences> =
            PreferenceDataStoreFactory.create(
                corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
                produceFile = { context.dataStoreFile("game_data.preferences_pb") }
            )

        @[Provides ApplicationScope] fun provideGson(): Gson = Gson()
    }
}
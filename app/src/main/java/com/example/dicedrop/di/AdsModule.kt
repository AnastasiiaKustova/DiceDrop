package com.example.dicedrop.di

import android.content.Context
import com.arkivanov.mvikotlin.core.BuildConfig
import com.example.dicedrop.data.ads.FakeRewardedAdManager
import com.example.dicedrop.data.ads.GoogleRewardedAdManager
import com.example.dicedrop.domain.ads.RewardedAdManager
import dagger.Module
import dagger.Provides

@Module
object AdsModule {

    @Provides
    @ApplicationScope
    fun provideRewardedAdManager(
        context: Context
    ): RewardedAdManager {
        return if (BuildConfig.DEBUG) {
            FakeRewardedAdManager()
        } else {
            GoogleRewardedAdManager(context)
        }
    }
}
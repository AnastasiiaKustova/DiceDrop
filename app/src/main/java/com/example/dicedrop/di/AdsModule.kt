package com.example.dicedrop.di

import android.content.Context
import com.example.dicedrop.data.ads.YandexRewardedAdManager
import com.example.dicedrop.domain.ads.RewardedAdManager
import dagger.Module
import dagger.Provides

@Module
object AdsModule {

    // В отладочной сборке блоки тестовые (см. build.gradle.kts)
    @Provides
    @ApplicationScope
    fun provideRewardedAdManager(
        context: Context
    ): RewardedAdManager {
        return YandexRewardedAdManager(context)
    }
}

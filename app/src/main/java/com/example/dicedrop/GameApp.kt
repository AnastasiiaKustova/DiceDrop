package com.example.dicedrop

import android.app.Application
import com.example.dicedrop.di.ApplicationComponent
import com.example.dicedrop.di.DaggerApplicationComponent
import com.yandex.mobile.ads.common.MobileAds

class GameApp: Application() {
    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this) {}
        applicationComponent = DaggerApplicationComponent.factory().create(this)
    }
}
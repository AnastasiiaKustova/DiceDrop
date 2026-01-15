package com.example.dicedrop

import android.app.Application
import com.example.dicedrop.di.ApplicationComponent
import com.example.dicedrop.di.DaggerApplicationComponent

class GameApp: Application() {
    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerApplicationComponent.factory().create(this)
    }
}
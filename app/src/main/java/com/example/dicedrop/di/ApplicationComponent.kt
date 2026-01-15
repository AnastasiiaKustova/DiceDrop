package com.example.dicedrop.di

import android.content.Context
import com.example.dicedrop.presentation.MainActivity
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(
    modules = [
        DataModule::class,
        AdsModule::class,
        PresentationModule::class
    ]
)
interface ApplicationComponent {

    fun inject(activity: MainActivity)

    @Component.Factory
    interface Factory{

        fun create(
            @BindsInstance context: Context
        ): ApplicationComponent
    }
}
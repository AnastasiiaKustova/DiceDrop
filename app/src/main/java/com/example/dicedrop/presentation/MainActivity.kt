package com.example.dicedrop.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.arkivanov.decompose.defaultComponentContext
import com.example.dicedrop.GameApp
import com.example.dicedrop.presentation.root.RootComponentImpl
import com.example.dicedrop.presentation.root.RootContent
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var rootComponentFactory: RootComponentImpl.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as GameApp).applicationComponent.inject(this)

        super.onCreate(savedInstanceState)

        setContent {

            RootContent(component = rootComponentFactory.create(
                onExitApp = { finish() },
                componentContext = defaultComponentContext()
            ))
        }
    }
}



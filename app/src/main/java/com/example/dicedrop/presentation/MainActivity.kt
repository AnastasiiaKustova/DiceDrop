package com.example.dicedrop.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.arkivanov.decompose.defaultComponentContext
import androidx.compose.runtime.CompositionLocalProvider
import com.example.dicedrop.GameApp
import com.example.dicedrop.domain.feedback.BackgroundMusic
import com.example.dicedrop.domain.feedback.GameFeedback
import com.example.dicedrop.presentation.common.LocalBackgroundMusic
import com.example.dicedrop.presentation.common.LocalGameFeedback
import com.example.dicedrop.presentation.root.RootComponentImpl
import com.example.dicedrop.presentation.root.RootContent
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var rootComponentFactory: RootComponentImpl.Factory

    @Inject
    lateinit var gameFeedback: GameFeedback

    @Inject
    lateinit var backgroundMusic: BackgroundMusic

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as GameApp).applicationComponent.inject(this)

        super.onCreate(savedInstanceState)

        // Корневой компонент создаём один раз, а не при каждой рекомпозиции
        val rootComponent = rootComponentFactory.create(
            onExitApp = { finish() },
            componentContext = defaultComponentContext()
        )

        setContent {
            CompositionLocalProvider(
                LocalGameFeedback provides gameFeedback,
                LocalBackgroundMusic provides backgroundMusic
            ) {
                RootContent(component = rootComponent)
            }
        }
    }

    // Музыка играет, только пока приложение на экране
    override fun onStart() {
        super.onStart()
        backgroundMusic.setForeground(true)
    }

    override fun onStop() {
        backgroundMusic.setForeground(false)
        super.onStop()
    }
}



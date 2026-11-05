package com.example.dicedrop.presentation

import android.content.Intent
import android.content.pm.ActivityInfo
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
        // Если игру уже запустили не с рабочего стола (из магазина после установки), значок открывает
        // её копию поверх. Закрываем копию, и на экране остаётся то, что было открыто, например ролик
        if (!isTaskRoot && intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intent.action == Intent.ACTION_MAIN) {
            super.onCreate(savedInstanceState)
            finish()
            return
        }

        (applicationContext as GameApp).applicationComponent.inject(this)

        super.onCreate(savedInstanceState)

        // Телефон держим в портретной ориентации, а на планшете экран поворачивается
        requestedOrientation = if (resources.configuration.smallestScreenWidthDp >= 600) {
            ActivityInfo.SCREEN_ORIENTATION_FULL_USER
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

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

    // Музыка играет, только пока игра на экране и активна: ролик рекламы открывается поверх неё
    override fun onResume() {
        super.onResume()
        backgroundMusic.setForeground(true)
    }

    override fun onPause() {
        backgroundMusic.setForeground(false)
        super.onPause()
    }
}



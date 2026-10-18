package com.example.dicedrop.domain.feedback

// Фоновая музыка. Играет, только когда она включена в настройках, приложение на экране и игра не на паузе
interface BackgroundMusic {

    // Приложение на экране / свернуто
    fun setForeground(isForeground: Boolean)

    // Игра на паузе
    fun setPaused(isPaused: Boolean)

    companion object {
        // Заглушка для превью и тестов
        val None = object : BackgroundMusic {
            override fun setForeground(isForeground: Boolean) = Unit
            override fun setPaused(isPaused: Boolean) = Unit
        }
    }
}

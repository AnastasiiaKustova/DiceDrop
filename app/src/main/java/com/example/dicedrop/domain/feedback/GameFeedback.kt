package com.example.dicedrop.domain.feedback

// Звуки и вибрация игры. Реализация сама проверяет, включены ли они в настройках
interface GameFeedback {

    fun play(sound: Sound)

    fun vibrate(haptic: Haptic)

    enum class Sound {
        DICE_ROLL,
        LINE_CLEAR,
        COMBO_X2,
        COMBO_X3,
        COMBO_X4,
        COMBO_X5,
        COIN,
        COINS_REWARD,
        BONUS_BUY,
        ACHIEVEMENT,
        NEW_RECORD,
        GAME_OVER,
        BUTTON_CLICK,
        NOT_ENOUGH_COINS,
        DAILY_GIFT,
        BONUS_ROTATE,
        BONUS_FREEZE,
        // Лёд на замороженных кубиках тает при следующем броске
        BONUS_THAW,
        // Выезжает панель выбора грани
        BONUS_LUCKY_SHEET,
        // Грань поставлена
        BONUS_LUCKY,
        // Третий кубик: лишний кубик убран
        BONUS_DISCARD
    }

    enum class Haptic {
        // Кубики приземлились
        TICK,
        // Блок встал на поле
        PLACE,
        // Блок не влез и вернулся в лоток
        REJECT,
        LINE,
        COMBO,
        BIG_COMBO,
        // Бонус сработал: поворот, заморозка, грань поставлена, лишний кубик убран
        BONUS,
        // Рекорд, достижение
        CELEBRATE,
        GAME_OVER
    }

    companion object {
        // Заглушка для превью и тестов
        val None = object : GameFeedback {
            override fun play(sound: Sound) = Unit
            override fun vibrate(haptic: Haptic) = Unit
        }

        // Очистка линий: звук и сила вибрации растут вместе с комбо
        fun GameFeedback.lineCleared(combo: Int) {
            play(
                when {
                    combo <= 1 -> Sound.LINE_CLEAR
                    combo == 2 -> Sound.COMBO_X2
                    combo == 3 -> Sound.COMBO_X3
                    combo == 4 -> Sound.COMBO_X4
                    else -> Sound.COMBO_X5
                }
            )
            vibrate(
                when {
                    combo <= 1 -> Haptic.LINE
                    combo < 4 -> Haptic.COMBO
                    else -> Haptic.BIG_COMBO
                }
            )
        }
    }
}

package com.example.dicedrop.domain.useCases

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateHelper {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun getCurrentDate(): String {
        return LocalDateTime.now().format(formatter)
    }

    fun getYesterdayDate(): String {
        return LocalDateTime.now().minusDays(1).format(formatter)
    }
}

package com.example.dicedrop.domain.useCases

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateHelper {
    fun getCurrentDate(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return LocalDateTime.now().format(formatter)
    }
}
package com.example.dicedrop.domain.useCases.shop

import com.example.dicedrop.domain.repository.ShopRepository
import com.example.dicedrop.domain.useCases.DateHelper
import javax.inject.Inject

class GetDailyRewardIsReceivedStateUseCase @Inject constructor(
    private val repository: ShopRepository
) {
    suspend operator fun invoke(date: String? = null): Boolean {
        return if (date.isNullOrBlank())
            repository.isDailyRewardReceived(DateHelper.getCurrentDate())
        else
            repository.isDailyRewardReceived(date)
    }
}
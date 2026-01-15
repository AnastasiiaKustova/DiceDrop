package com.example.dicedrop.domain.useCases.shop

import com.example.dicedrop.domain.repository.ShopRepository
import javax.inject.Inject

class GetDailyRewardUseCase @Inject constructor(
    private val repository: ShopRepository
) {
    suspend operator fun invoke() = repository.getDailyReward()
}
package com.example.dicedrop.domain.useCases.shop

import com.example.dicedrop.domain.repository.ShopRepository
import javax.inject.Inject

class ObserveDailyRewardUseCase @Inject constructor(
    private val repository: ShopRepository
) {
    operator fun invoke() = repository.rewardFlow
}
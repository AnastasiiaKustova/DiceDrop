package com.example.dicedrop.data.ads

import android.app.Activity
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.example.dicedrop.BuildConfig
import com.example.dicedrop.domain.ads.RewardedAdManager
import com.example.dicedrop.domain.ads.RewardedAdManager.RewardedAdState
import com.example.dicedrop.domain.entity.AdReward
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.rewarded.Reward
import com.yandex.mobile.ads.rewarded.RewardedAd
import com.yandex.mobile.ads.rewarded.RewardedAdEventListener
import com.yandex.mobile.ads.rewarded.RewardedAdLoadListener
import com.yandex.mobile.ads.rewarded.RewardedAdLoader
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Сколько ждём сообщения о закрытии ролика после возвращения на экран игры
private const val AD_CLOSE_WAIT_MS = 1500L

// Реклама с вознаграждением из Рекламной сети Яндекса. На каждую награду свой блок,
// ролики загружаются заранее, чтобы по нажатию показываться сразу
class YandexRewardedAdManager(
    context: Context
) : RewardedAdManager {

    private val _state = MutableStateFlow<RewardedAdState>(RewardedAdState.Idle)
    override val state: StateFlow<RewardedAdState> = _state

    private val scope = MainScope()

    // Нажатие, которое ждёт загрузки ролика
    private class Request(
        val activity: Activity,
        val reward: AdReward,
        val onReward: () -> Unit,
        val onUnavailable: () -> Unit
    )

    private inner class Slot(private val reward: AdReward, context: Context) {
        private val loader = RewardedAdLoader(context)
        private var isLoading = false
        var ad: RewardedAd? = null

        init {
            loader.setAdLoadListener(object : RewardedAdLoadListener {
                override fun onAdLoaded(rewarded: RewardedAd) {
                    isLoading = false
                    ad = rewarded
                    pending?.takeIf { it.reward == reward }?.let {
                        pending = null
                        show(it)
                    }
                }

                // Повторная загрузка не сразу, а при следующем нажатии
                override fun onAdFailedToLoad(error: AdRequestError) {
                    isLoading = false
                    pending?.takeIf { it.reward == reward }?.let {
                        pending = null
                        _state.value = RewardedAdState.Idle
                        it.onUnavailable()
                    }
                }
            })
        }

        fun load() {
            if (isLoading || ad != null) return
            isLoading = true
            loader.loadAd(AdRequestConfiguration.Builder(reward.adUnitId()).build())
        }
    }

    private val slots = AdReward.entries.associateWith { Slot(it, context) }
    private var pending: Request? = null

    init {
        slots.values.forEach { it.load() }
    }

    override fun showAd(
        activity: Activity,
        reward: AdReward,
        onReward: () -> Unit,
        onUnavailable: () -> Unit
    ) {
        if (_state.value != RewardedAdState.Idle) return
        val request = Request(activity, reward, onReward, onUnavailable)
        val slot = slots.getValue(reward)
        if (slot.ad != null) {
            show(request)
        } else {
            pending = request
            _state.value = RewardedAdState.Loading
            slot.load()
        }
    }

    private fun show(request: Request) {
        val slot = slots.getValue(request.reward)
        val ad = slot.ad ?: return
        slot.ad = null
        if (request.activity.isFinishing) {
            _state.value = RewardedAdState.Idle
            return
        }
        var isRewarded = false
        var isFinished = false
        val lifecycle = (request.activity as? LifecycleOwner)?.lifecycle

        // Ролик идёт поверх игры, поэтому экран игры возвращается, только когда ролика уже нет. Если сворачивать
        // игру во время ролика, сообщение о закрытии может не прийти: без этого окно «Просмотр рекламы» висело бы вечно
        lateinit var finish: () -> Unit
        val returnObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                scope.launch {
                    delay(AD_CLOSE_WAIT_MS)
                    finish()
                }
            }
        }

        // Награду выдаём после закрытия ролика, когда игрок уже вернулся в игру
        finish = {
            if (!isFinished) {
                isFinished = true
                lifecycle?.removeObserver(returnObserver)
                ad.setAdEventListener(null)
                slot.load()
                if (isRewarded) {
                    _state.value = RewardedAdState.Rewarded
                    scope.launch {
                        delay(600)
                        request.onReward()
                        _state.value = RewardedAdState.Idle
                    }
                } else {
                    _state.value = RewardedAdState.Idle
                }
            }
        }

        ad.setAdEventListener(object : RewardedAdEventListener {
            override fun onAdShown() {
                if (!isFinished) _state.value = RewardedAdState.Showing
            }

            override fun onAdFailedToShow(adError: AdError) {
                isFinished = true
                lifecycle?.removeObserver(returnObserver)
                ad.setAdEventListener(null)
                _state.value = RewardedAdState.Idle
                request.onUnavailable()
                slot.load()
            }

            override fun onRewarded(reward: Reward) {
                isRewarded = true
            }

            override fun onAdDismissed() = finish()

            override fun onAdClicked() = Unit

            override fun onAdImpression(impressionData: ImpressionData?) = Unit
        })
        _state.value = RewardedAdState.Showing
        ad.show(request.activity)
        // Экран игры сейчас ещё активен: подписка сообщит о возвращении только после ухода под ролик
        scope.launch {
            lifecycle?.let { current ->
                while (!isFinished && current.currentState.isAtLeast(Lifecycle.State.RESUMED)) delay(100)
                if (!isFinished) current.addObserver(returnObserver)
            }
        }
    }

    private fun AdReward.adUnitId() = when (this) {
        AdReward.REVIVE -> BuildConfig.AD_UNIT_REVIVE
        AdReward.DOUBLE_COINS -> BuildConfig.AD_UNIT_DOUBLE_COINS
        AdReward.RANDOM_BONUS -> BuildConfig.AD_UNIT_RANDOM_BONUS
    }
}

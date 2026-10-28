package com.example.dicedrop.presentation.shop.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.unit.Dp
import com.example.dicedrop.presentation.common.isLandscape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import com.example.dicedrop.domain.catalog.EconomyCatalog
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
import com.example.dicedrop.domain.catalog.ShopItemCatalog
import com.example.dicedrop.domain.entity.BonusItem
import com.example.dicedrop.domain.entity.BonusType
import com.example.dicedrop.domain.entity.ShopItem
import com.example.dicedrop.presentation.common.GameButton
import com.example.dicedrop.presentation.common.GameIcons
import com.example.dicedrop.presentation.common.IconCircle
import com.example.dicedrop.presentation.menu.claimedInWeek
import com.example.dicedrop.presentation.ui.theme.ButtonText
import com.example.dicedrop.presentation.ui.theme.DarkBlue
import com.example.dicedrop.presentation.ui.theme.DeepBlue
import com.example.dicedrop.presentation.ui.theme.Green
import com.example.dicedrop.presentation.ui.theme.GreenText
import com.example.dicedrop.presentation.ui.theme.MidBlue
import com.example.dicedrop.presentation.ui.theme.White
import com.example.dicedrop.presentation.ui.theme.Yellow

// Порядок бонусов как в игре
private val bonusOrder = listOf(BonusType.ROTATE, BonusType.FREEZE, BonusType.LUCKY, BonusType.DRAFT)

@Composable
fun ShopItems(
    dailyRewardClaimed: Boolean,
    dayStreak: Int,
    bonuses: List<BonusItem>,
    adBonusesLeft: Int,
    onCardClick: (ShopItem) -> Unit,
    onAdBonusClick: () -> Unit
) {
    val shopItems = ShopItemCatalog.all
    val dailyItem = shopItems.first { it.id == "daily_bonus" }
    val randomItem = shopItems.first { it.id == "random_bonus" }
    val bonusItems = bonusOrder.mapNotNull { type -> shopItems.firstOrNull { it.bonusType == type } }

    if (isLandscape()) {
        // Планшет: подарок строкой сверху, под ним все пять бонусов в один ряд
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(start = 40.dp, end = 40.dp, top = 18.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DailyRewardRow(
                claimed = dailyRewardClaimed,
                dayStreak = dayStreak,
                onClaimClick = { onCardClick(dailyItem) }
            )
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = stringResource(R.string.bonuses_title),
                color = White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black
            )
            Row(
                modifier = Modifier.height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                bonusItems.forEach { item ->
                    val type = item.bonusType ?: return@forEach
                    BonusCard(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        bonusType = type,
                        price = item.price,
                        owned = bonuses.firstOrNull { it.type == type }?.count ?: 0,
                        large = true,
                        onBuyClick = { onCardClick(item) }
                    )
                }
                RandomBonusCard(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    price = randomItem.price,
                    adBonusesLeft = adBonusesLeft,
                    onBuyClick = { onCardClick(randomItem) },
                    onAdClick = onAdBonusClick
                )
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(0.6f),
                text = stringResource(R.string.owned_hint),
                color = White,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Пока подарок не забран, он сверху; забранный уезжает вниз, чтобы бонусы помещались на экран
        val dailyCard = @Composable {
            DailyRewardCard(
                claimed = dailyRewardClaimed,
                dayStreak = dayStreak,
                onClaimClick = { onCardClick(dailyItem) }
            )
        }
        if (!dailyRewardClaimed) dailyCard()

        Text(
            modifier = Modifier.padding(top = 2.dp),
            text = stringResource(R.string.bonuses_title),
            color = White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black
        )

        bonusItems.chunked(2).forEach { row ->
            // IntrinsicSize.Max: обе карточки в ряду по высоте самой высокой
            Row(
                modifier = Modifier.height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { item ->
                    val type = item.bonusType ?: return@forEach
                    BonusCard(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        bonusType = type,
                        price = item.price,
                        owned = bonuses.firstOrNull { it.type == type }?.count ?: 0,
                        onBuyClick = { onCardClick(item) }
                    )
                }
            }
        }

        RandomBonusRow(
            price = randomItem.price,
            adBonusesLeft = adBonusesLeft,
            onBuyClick = { onCardClick(randomItem) },
            onAdClick = onAdBonusClick
        )

        if (dailyRewardClaimed) dailyCard()

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(0.6f),
            text = stringResource(R.string.owned_hint),
            color = White,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DailyRewardCard(
    claimed: Boolean,
    dayStreak: Int,
    onClaimClick: () -> Unit
) {
    val doneDays = dayStreak.claimedInWeek(claimed)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MidBlue, RoundedCornerShape(24.dp))
            .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconCircle(iconRes = R.drawable.ic_gift_box, size = 60.dp, iconSize = 45.dp)
            Column {
                Text(
                    text = stringResource(R.string.daily_gift),
                    color = White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    modifier = Modifier.alpha(0.85f),
                    text = stringResource(R.string.daily_gift_subtitle),
                    color = White,
                    fontSize = 13.sp
                )
            }
        }

        DaysRow(dayStreak = dayStreak, doneDays = doneDays, claimed = claimed)

        ClaimButton(claimed = claimed, modifier = Modifier.fillMaxWidth(), onClaimClick = onClaimClick)
    }
}

// Планшет: подарок одной строкой - подпись, дни недели и кнопка
@Composable
private fun DailyRewardRow(
    claimed: Boolean,
    dayStreak: Int,
    onClaimClick: () -> Unit
) {
    val doneDays = dayStreak.claimedInWeek(claimed)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MidBlue, RoundedCornerShape(26.dp))
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        Row(
            modifier = Modifier.weight(0.9f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            IconCircle(iconRes = R.drawable.ic_gift_box, size = 66.dp, iconSize = 49.dp)
            Column {
                Text(
                    text = stringResource(R.string.daily_gift),
                    color = White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    modifier = Modifier.alpha(0.85f),
                    text = stringResource(R.string.daily_gift_subtitle),
                    color = White,
                    fontSize = 14.sp
                )
            }
        }
        Box(modifier = Modifier.weight(1.2f)) {
            DaysRow(dayStreak = dayStreak, doneDays = doneDays, claimed = claimed, height = 50.dp)
        }
        ClaimButton(claimed = claimed, modifier = Modifier.width(220.dp), height = 54.dp, onClaimClick = onClaimClick)
    }
}

@Composable
private fun ClaimButton(
    claimed: Boolean,
    modifier: Modifier,
    height: Dp = 46.dp,
    onClaimClick: () -> Unit
) {
    if (claimed) {
        GameButton(
            modifier = modifier,
            text = stringResource(R.string.claimed),
            onClick = {},
            enabled = false,
            color = DeepBlue,
            contentColor = White,
            height = height,
            corner = 16.dp
        )
    } else {
        GameButton(
            modifier = modifier,
            text = stringResource(R.string.claim),
            onClick = onClaimClick,
            height = height,
            corner = 16.dp
        )
    }
}

// Дни недели: забранные - зелёные с галкой, сегодняшний - жёлтый, остальные - с суммой.
// Суммы - для той недели серии, в которой сегодняшний день: со второй недели везде максимум
@Composable
private fun DaysRow(dayStreak: Int, doneDays: Int, claimed: Boolean, height: Dp = 42.dp) {
    val todayInStreak = if (claimed) dayStreak else dayStreak + 1
    val weekStart = (todayInStreak - 1).coerceAtLeast(0) / EconomyCatalog.WEEK_DAYS * EconomyCatalog.WEEK_DAYS
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        for (day in 1..EconomyCatalog.WEEK_DAYS) {
            val done = day <= doneDays
            val today = !claimed && day == doneDays + 1
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(height)
                    .background(
                        color = when {
                            done -> Green
                            today -> Yellow
                            else -> DarkBlue
                        },
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (done) {
                    Icon(
                        imageVector = GameIcons.Check,
                        contentDescription = null,
                        tint = GreenText,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    // Сколько монет даёт день; в последний день недели ещё и бонус - на нём подарок
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(
                                if (day == EconomyCatalog.WEEK_DAYS) R.drawable.ic_gift_box else R.drawable.coin_star
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = EconomyCatalog.dailyCoins(weekStart + day).toString(),
                            color = if (today) ButtonText else White,
                            fontSize = 14.sp,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

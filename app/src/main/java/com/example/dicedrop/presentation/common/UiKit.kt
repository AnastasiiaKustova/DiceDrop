package com.example.dicedrop.presentation.common

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicedrop.R
import com.example.dicedrop.domain.feedback.GameFeedback
import com.example.dicedrop.presentation.ui.theme.ButtonText
import com.example.dicedrop.presentation.ui.theme.CoinEdge
import com.example.dicedrop.presentation.ui.theme.CoinGold
import com.example.dicedrop.presentation.ui.theme.CoinShade
import com.example.dicedrop.presentation.ui.theme.DeepBlue
import com.example.dicedrop.presentation.ui.theme.DieBorder
import com.example.dicedrop.presentation.ui.theme.DieShade
import com.example.dicedrop.presentation.ui.theme.DieWhite
import com.example.dicedrop.presentation.ui.theme.Navy
import com.example.dicedrop.presentation.ui.theme.Scrim
import com.example.dicedrop.presentation.ui.theme.White
import com.example.dicedrop.presentation.ui.theme.Yellow

// Число с пробелами между разрядами: 12450 -> "12 450"
fun Int.spaced(): String {
    val digits = kotlin.math.abs(this).toString().reversed().chunked(3).joinToString(" ").reversed()
    return if (this < 0) "-$digits" else digits
}

// Кубик в новом стиле: белая плашка с обводкой, тенью снизу и точками
@Composable
fun DieFace(
    value: Int?,
    size: Dp,
    modifier: Modifier = Modifier,
    gold: Boolean = false,
    glow: Float = 0f
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawDie(value = value ?: 0, gold = gold, glow = glow)
        }
        if (value == null || value == 0) {
            Text(
                text = "?",
                color = Navy,
                fontSize = (size.value * 0.5f).sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

fun DrawScope.drawDie(value: Int, gold: Boolean = false, glow: Float = 0f) {
    val s = size.minDimension
    val radius = s * 0.25f
    val border = (s * 0.06f).coerceAtLeast(2.dp.toPx())

    if (glow > 0f) {
        val far = 10.dp.toPx()
        val near = 4.dp.toPx()
        drawRoundRect(
            color = Yellow.copy(alpha = 0.3f * glow),
            topLeft = Offset(-far, -far),
            size = Size(s + far * 2, s + far * 2),
            cornerRadius = CornerRadius(radius + far)
        )
        drawRoundRect(
            color = Yellow.copy(alpha = 0.75f * glow),
            topLeft = Offset(-near, -near),
            size = Size(s + near * 2, s + near * 2),
            cornerRadius = CornerRadius(radius + near)
        )
    }

    val face = if (gold) CoinGold else DieWhite
    val edge = if (gold) CoinEdge else DieBorder
    val shade = if (gold) CoinShade else DieShade

    // Обводка, затем грань с нижней "толщиной"
    drawRoundRect(color = edge, cornerRadius = CornerRadius(radius))
    val inner = Size(s - border * 2, s - border * 2)
    drawRoundRect(
        color = shade,
        topLeft = Offset(border, border),
        size = inner,
        cornerRadius = CornerRadius(radius - border)
    )
    drawRoundRect(
        color = face,
        topLeft = Offset(border, border),
        size = Size(inner.width, inner.height - s * 0.1f),
        cornerRadius = CornerRadius(radius - border)
    )

    val pip = s * 0.185f
    val near = s * 0.18f + pip / 2
    val far = s - near
    val mid = s / 2
    val lift = s * 0.045f
    val points = when (value) {
        1 -> listOf(mid to mid)
        2 -> listOf(near to near, far to far)
        3 -> listOf(near to near, mid to mid, far to far)
        4 -> listOf(near to near, far to near, near to far, far to far)
        5 -> listOf(near to near, far to near, mid to mid, near to far, far to far)
        6 -> listOf(near to near, far to near, near to mid, far to mid, near to far, far to far)
        else -> emptyList()
    }
    points.forEach { (x, y) ->
        drawCircle(color = Navy, radius = pip / 2, center = Offset(x, y - lift))
    }
}

// Плашка с количеством монет
@Composable
fun CoinChip(
    coins: Int,
    modifier: Modifier = Modifier,
    height: Dp = 44.dp,
    fontSize: TextUnit = 18.sp,
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .height(height)
            .background(DeepBlue, CircleShape)
            .padding(start = height * 0.08f, end = if (trailing == null) height * 0.3f else 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.coin_star),
            contentDescription = null,
            modifier = Modifier.size(height * 0.7f)
        )
        Text(
            text = coins.spaced(),
            color = White,
            fontSize = fontSize,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1
        )
        trailing?.invoke()
    }
}

// Маленький ценник: монета и цена
@Composable
fun PriceTag(
    price: Int,
    modifier: Modifier = Modifier,
    background: Color = DeepBlue,
    contentColor: Color = White,
    height: Dp = 24.dp,
    fontSize: TextUnit = 13.sp
) {
    Row(
        modifier = modifier
            .height(height)
            .background(background, CircleShape)
            .padding(start = 2.dp, end = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.coin_star),
            contentDescription = null,
            modifier = Modifier.size(height * 0.8f)
        )
        Text(
            text = price.toString(),
            color = contentColor,
            fontSize = fontSize,
            fontWeight = FontWeight.Black,
            maxLines = 1
        )
    }
}

// Основная кнопка: жёлтая по умолчанию, вторичная - синяя
@Composable
fun GameButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Yellow,
    contentColor: Color = ButtonText,
    height: Dp = 52.dp,
    corner: Dp = 18.dp,
    fontSize: TextUnit = 19.sp,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    iconSize: Dp = 22.dp
) {
    val feedback = LocalGameFeedback.current
    Row(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(corner))
            .background(color)
            .clickable(enabled = enabled) {
                feedback.play(GameFeedback.Sound.BUTTON_CLICK)
                onClick()
            }
            .padding(horizontal = 14.dp)
            .alpha(if (enabled) 1f else 0.6f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(iconSize)
            )
        }
        Text(
            text = text,
            color = contentColor,
            fontSize = fontSize,
            fontWeight = FontWeight.Black,
            maxLines = 1
        )
    }
}

// Кремовый круг с иконкой; ring - синее кольцо вокруг
@Composable
fun IconCircle(
    @DrawableRes iconRes: Int,
    size: Dp,
    iconSize: Dp,
    modifier: Modifier = Modifier,
    ring: Dp = 0.dp,
    ringColor: Color = Color.Transparent,
    background: Color = White,
    iconAlpha: Float = 1f
) {
    Box(
        modifier = modifier
            .size(size + ring * 2)
            .background(ringColor, CircleShape)
            .padding(ring)
            .background(background, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier
                .size(iconSize)
                .alpha(iconAlpha)
        )
    }
}

// Круглая кнопка-картинка (назад, настройки, пауза, достижения)
@Composable
fun RoundImageButton(
    @DrawableRes iconRes: Int,
    contentDescription: String?,
    size: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val feedback = LocalGameFeedback.current
    Image(
        painter = painterResource(iconRes),
        contentDescription = contentDescription,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .clickable {
                feedback.play(GameFeedback.Sound.BUTTON_CLICK)
                onClick()
            }
    )
}

@Composable
fun ProgressBar(
    fraction: Float,
    color: Color,
    modifier: Modifier = Modifier,
    height: Dp = 10.dp,
    track: Color = DeepBlue
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(CircleShape)
            .background(track)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction.coerceIn(0f, 1f))
                .clip(CircleShape)
                .background(color)
        )
    }
}

// Альбомная ориентация бывает только на планшетах: телефоны закреплены в портретной
@Composable
fun isLandscape(): Boolean =
    LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

// Затемнение под окном; касания к экрану под ним не проходят.
// maxWidth не даёт окнам растянуться на всю ширину планшета
@Composable
fun DialogScrim(
    modifier: Modifier = Modifier,
    color: Color = Scrim,
    contentAlignment: Alignment = Alignment.Center,
    maxWidth: Dp = 460.dp,
    onDismiss: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color)
            .then(
                if (onDismiss != null) Modifier.clickable(
                    interactionSource = interaction,
                    indication = null,
                    onClick = onDismiss
                ) else Modifier.pointerInput(Unit) {}
            ),
        contentAlignment = contentAlignment
    ) {
        Box(
            modifier = Modifier.widthIn(max = maxWidth),
            contentAlignment = contentAlignment,
            content = content
        )
    }
}

// Карточка окна не должна закрывать окно касанием по себе
fun Modifier.consumeClicks(): Modifier = this.pointerInput(Unit) {}

// Иконки из макета (сетка 24x24), красятся через tint
object GameIcons {

    val Play: ImageVector by lazy {
        icon("play", fill = listOf("M7 4.5v15a1 1 0 0 0 1.5.86l12.5-7.5a1 1 0 0 0 0-1.72L8.5 3.64A1 1 0 0 0 7 4.5z"))
    }

    val Check: ImageVector by lazy {
        icon("check", stroke = listOf("M20 6 9 17l-5-5"), width = 3.4f)
    }

    val Undo: ImageVector by lazy {
        icon("undo", stroke = listOf("M9 14 4 9l5-5", "M4 9h10.5a5.5 5.5 0 0 1 0 11H11"))
    }

    val Restart: ImageVector by lazy {
        icon("restart", stroke = listOf("M4 12a8 8 0 1 0 2.4-5.7", "M4 4v4.5h4.5"))
    }

    val Exit: ImageVector by lazy {
        icon("exit", stroke = listOf("M14 4H6v16h8", "M11 12h10M17 8l4 4-4 4"))
    }

    val Close: ImageVector by lazy {
        icon("close", stroke = listOf("M5 5l14 14M19 5 5 19"), width = 3.6f)
    }

    val Back: ImageVector by lazy {
        icon("back", stroke = listOf("M15 5l-7 7 7 7"), width = 3.4f)
    }

    val Skip: ImageVector by lazy {
        icon("skip", stroke = listOf("M5 5l7 7-7 7", "M13 5l7 7-7 7"), width = 3.4f)
    }

    val ArrowRight: ImageVector by lazy {
        icon("arrowRight", stroke = listOf("M5 12h14", "M13 6l6 6-6 6"))
    }

    val Snowflake: ImageVector by lazy {
        icon("snowflake", stroke = listOf("M12 2v20", "M4 6l16 12", "M20 6 4 18"))
    }

    // Знак вопроса - кнопка «Как играть»
    val Help: ImageVector by lazy {
        icon("help", stroke = listOf("M8.5 8.5a3.5 3.5 0 0 1 7 0.5c0 2.5-3.5 3-3.5 5.5", "M12 19.5v.01"))
    }

    // Динамик с волнами
    val Sound: ImageVector by lazy {
        icon(
            "sound",
            stroke = listOf("M16 8.5a5 5 0 0 1 0 7", "M18.8 5.8a9 9 0 0 1 0 12.4"),
            fill = listOf("M3 9.5v5a1 1 0 0 0 1 1h3l5 4V4.5l-5 4H4a1 1 0 0 0-1 1z"),
            width = 2.4f
        )
    }

    // Две ноты
    val Music: ImageVector by lazy {
        icon(
            "music",
            stroke = listOf("M9 17V5l11-2v12"),
            fill = listOf(
                "M3.3,17.5a3.2,3.2 0 1,0 6.4,0a3.2,3.2 0 1,0 -6.4,0",
                "M14.3,15.5a3.2,3.2 0 1,0 6.4,0a3.2,3.2 0 1,0 -6.4,0"
            ),
            width = 2.6f
        )
    }

    // Телефон с чёрточками вибрации по бокам
    val Vibration: ImageVector by lazy {
        icon(
            "vibration",
            stroke = listOf(
                "M9.5 3.5h5a1.5 1.5 0 0 1 1.5 1.5v14a1.5 1.5 0 0 1-1.5 1.5h-5A1.5 1.5 0 0 1 8 19V5a1.5 1.5 0 0 1 1.5-1.5z",
                "M4.5 8.5v7",
                "M19.5 8.5v7"
            ),
            width = 2.4f
        )
    }

    private fun icon(
        name: String,
        stroke: List<String> = emptyList(),
        fill: List<String> = emptyList(),
        evenOdd: String? = null,
        width: Float = 3f
    ): ImageVector = ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        stroke.forEach {
            addPath(
                pathData = addPathNodes(it),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = width,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            )
        }
        fill.forEach {
            addPath(pathData = addPathNodes(it), fill = SolidColor(Color.Black))
        }
        if (evenOdd != null) {
            addPath(
                pathData = addPathNodes(evenOdd),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black)
            )
        }
    }.build()
}

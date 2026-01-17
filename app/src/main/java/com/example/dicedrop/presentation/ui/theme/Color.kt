package com.example.dicedrop.presentation.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val CellBG = Color(0xFF6293D1)
val Yellow = Color(0xFFFFD167)
val YellowDark = Color(0xFFFCDDA4)
val Orange = Color(0xFFF78C6A)
val White = Color(0xFFFFF9EB)
val WhiteBlue = Color(0xFFD0DAE3)
val DarkBlue = Color(0xFF3A5EA7)
val Blue = Color(0xFF8EA3CC)

val Blue2 = Color(0xFFDAE3E8)

val LightBlue = Color(0xFF73AAE2)
val Green = Color(0xFF9DF092)
val Red = Color(0xFFF0475E)

val BlueGradientLight = Color(0xFF3A5EA6)
val BlueGradientDark = Color(0xFF4773CC)

val BlueGradient = Brush.linearGradient(
    colors = listOf(BlueGradientDark, BlueGradientLight),
    start = Offset(0f, 0f),
    end = Offset(0f, Float.POSITIVE_INFINITY)
)
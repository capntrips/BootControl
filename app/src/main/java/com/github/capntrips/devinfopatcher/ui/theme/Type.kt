package com.github.capntrips.devinfopatcher.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable

@Composable
fun typography(colorScheme: ColorScheme) : Typography {
    return Typography(
        labelMedium = Typography().labelMedium.copy(
            color = colorScheme.onSurface.copy(alpha = 0.667f)
        )
    )
}

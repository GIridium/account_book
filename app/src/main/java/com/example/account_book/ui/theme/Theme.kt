// app/src/main/java/com/example/account_book/ui/theme/Theme.kt
package com.example.account_book.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// 蓝色主题 - 亮色模式（降低饱和度版本）
val BlueLightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = Color.White,
    primaryContainer = BlueLight,
    onPrimaryContainer = BlueDark,
    secondary = Teal,
    onSecondary = Color.White,
    secondaryContainer = BlueExtraLight,
    onSecondaryContainer = BlueDark,
    tertiary = BlueDark,
    background = BlueExtraLight,
    onBackground = BlueDark,
    surface = Color.White,
    onSurface = Color(0xFF3A3A3A),           // 深化文字颜色，保持可读性
    surfaceVariant = BlueLight,
    onSurfaceVariant = BlueDark,
    error = ExpenseRed,
    onError = Color.White,
    outline = Color(0xFFB8B8B8)              // 柔和的边框颜色
)

// 蓝色主题 - 暗色模式
val BlueDarkColorScheme = darkColorScheme(
    primary = BlueLight,
    onPrimary = BlueDark,
    primaryContainer = BlueDark.copy(alpha = 0.5f),
    onPrimaryContainer = BlueLight,
    secondary = Teal.copy(alpha = 0.8f),
    onSecondary = Color.Black,
    secondaryContainer = BlueDark,
    onSecondaryContainer = BlueLight,
    tertiary = BlueLight,
    background = Color(0xFF121212),
    onBackground = BlueLight,
    surface = Color(0xFF1E1E1E),
    onSurface = BlueLight,
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color(0xFFB0B0B0),
    error = ExpenseRed.copy(alpha = 0.8f),
    onError = Color.White,
    outline = Color(0xFF444444)
)

@Composable
fun AccountBookTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> BlueDarkColorScheme
        else -> BlueLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view)?.isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
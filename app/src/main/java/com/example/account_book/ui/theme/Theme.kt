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

// 蓝色主题 - 亮色模式
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
    onSurface = BlueDark,
    error = ExpenseRed,
    onError = Color.White,
    outline = BlueLight
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
    error = ExpenseRed.copy(alpha = 0.8f),
    onError = Color.White
)

@Composable
fun AccountBookTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> BlueDarkColorScheme
        else -> BlueLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
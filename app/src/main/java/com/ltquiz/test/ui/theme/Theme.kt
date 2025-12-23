/*
 * Copyright 2024 LTQuiz Test
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ltquiz.test.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = TealPrimary,
    onPrimary = White,
    primaryContainer = TealLight,
    onPrimaryContainer = TealDark,
    secondary = Gray600,
    onSecondary = White,
    secondaryContainer = Gray200,
    onSecondaryContainer = Gray800,
    tertiary = Info,
    onTertiary = White,
    tertiaryContainer = Gray100,
    onTertiaryContainer = Gray700,
    error = Error,
    onError = White,
    errorContainer = Color(0xFFFFEBEE),
    onErrorContainer = Color(0xFFB71C1C),
    background = White,
    onBackground = Gray900,
    surface = White,
    onSurface = Gray900,
    surfaceVariant = Gray100,
    onSurfaceVariant = Gray700,
    outline = Gray400,
    outlineVariant = Gray300,
    scrim = Black,
    inverseSurface = Gray800,
    inverseOnSurface = Gray100,
    inversePrimary = TealLight,
    surfaceDim = Gray200,
    surfaceBright = White,
    surfaceContainerLowest = White,
    surfaceContainerLow = Gray50,
    surfaceContainer = Gray100,
    surfaceContainerHigh = Gray200,
    surfaceContainerHighest = Gray300
)

private val DarkColorScheme = darkColorScheme(
    primary = TealLight,
    onPrimary = TealDark,
    primaryContainer = TealDark,
    onPrimaryContainer = TealLight,
    secondary = Gray400,
    onSecondary = Gray900,
    secondaryContainer = Gray700,
    onSecondaryContainer = Gray200,
    tertiary = Color(0xFF64B5F6),
    onTertiary = Color(0xFF0D47A1),
    tertiaryContainer = Gray800,
    onTertiaryContainer = Gray300,
    error = Color(0xFFEF5350),
    onError = Color(0xFFB71C1C),
    errorContainer = Color(0xFFD32F2F),
    onErrorContainer = Color(0xFFFFCDD2),
    background = Gray900,
    onBackground = Gray100,
    surface = Gray900,
    onSurface = Gray100,
    surfaceVariant = Gray800,
    onSurfaceVariant = Gray300,
    outline = Gray600,
    outlineVariant = Gray700,
    scrim = Black,
    inverseSurface = Gray200,
    inverseOnSurface = Gray800,
    inversePrimary = TealPrimary,
    surfaceDim = Gray900,
    surfaceBright = Gray700,
    surfaceContainerLowest = Gray900,
    surfaceContainerLow = Gray800,
    surfaceContainer = Gray700,
    surfaceContainerHigh = Gray600,
    surfaceContainerHighest = Gray500
)

@Composable
fun LTQuizTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
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
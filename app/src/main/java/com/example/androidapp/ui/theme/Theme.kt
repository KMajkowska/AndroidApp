package com.example.androidapp.ui.theme

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
import java.util.Locale

private val DarkColorScheme = darkColorScheme(
    primary = Purple,
    secondary = DarkerPurple,
    onSurface = White,
    surface = Black,
    background = Black,
    onBackground = White,
    onPrimary = Black,
    onSecondary = White,
    primaryContainer = LighterPurple,
    secondaryContainer = LighterPurple,
    onPrimaryContainer =  Black,
    onSecondaryContainer = Color.Gray,
)

private val LightColorScheme = lightColorScheme(
    primary = Blue,
    onPrimary = White,
    secondary = darkerBlue,
    onSecondary = White,
    background = White,
    onBackground = Color.Black,
    onSurface = Color.Black,
    surface = White,
    primaryContainer = LighterBlue,
    secondaryContainer = LighterBlue,
    onPrimaryContainer = White,
    onSecondaryContainer = darkGray,
)

private val UnicornColorScheme = lightColorScheme(
    primary = Pink,
    onPrimary = White,
    secondary = darkerPink,
    onSecondary = White,
    background = White,
    onBackground = Color.Black,
    onSurface = Color.Black,
    surface = White,
    primaryContainer = LighterPink,
    secondaryContainer = LighterPink,
    onPrimaryContainer = Color.Black,
    onSecondaryContainer = darkGray,
    )

private val DarkUnicornScheme = darkColorScheme(
    primary = Pink,
    secondary = darkerPink,
    onSurface = White,
    surface = Black,
    background = Black,
    onBackground = White,
    onPrimary = Black,
    onSecondary = White,
    primaryContainer = LighterPink,
    secondaryContainer = LighterPink,
    onPrimaryContainer =  Black,
    onSecondaryContainer = darkGray,
)



@Composable
fun LanguageAwareScreen(selectedLanguage: String, content: @Composable () -> Unit) {
    val context = LocalContext.current

    val locale = Locale(selectedLanguage)
    Locale.setDefault(locale)
    val config = context.resources.configuration
    config.setLocale(locale)
    context.resources.updateConfiguration(config, context.resources.displayMetrics)

    content()
}

@Composable
fun AndroidAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    uniqrnTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme =
        if (darkTheme) {
            if (uniqrnTheme){
                DarkUnicornScheme
            }
            else {
                DarkColorScheme
            }
        }
        else if (uniqrnTheme){
            UnicornColorScheme
        }
        else {
            LightColorScheme
        }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            window.navigationBarColor = colorScheme.secondary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
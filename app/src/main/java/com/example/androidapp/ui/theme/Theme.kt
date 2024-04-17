package com.example.androidapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.androidapp.R
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
    tertiaryContainer = Color.hsv(251.75F, 0.5026F, 0.7569F),
    onTertiaryContainer = Color.hsv(270.41F, 0.6549F, 0.8863F),
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
    onSecondaryContainer = Color.Gray,
    tertiaryContainer = Color.hsv(183.33F, 0.2118F, 1F),
    onTertiaryContainer = Color.hsv(200.53F, 0.149F, 1F),
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
    onPrimaryContainer = White,
    onSecondaryContainer = Color.Gray,
    tertiaryContainer = Color.hsv(312.5F, 0.2824F, 1F),
    onTertiaryContainer = Color.hsv(312.35F, 0.1349F, 1F),
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
    onSecondaryContainer = Color.Gray,
    tertiaryContainer = Color.hsv(312.21F, 0.7107F, 0.6235F),
    onTertiaryContainer = Color.hsv(322.82F, 0.7978F, 0.698F),
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
            window.statusBarColor = colorScheme.secondary.toArgb()
            window.navigationBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
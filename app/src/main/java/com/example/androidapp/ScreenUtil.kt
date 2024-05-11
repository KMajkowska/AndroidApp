package com.example.androidapp

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidapp.settings.SettingsRepository
import com.example.androidapp.settings.SettingsViewModel
import com.example.androidapp.settings.SettingsViewModelFactory
import com.example.androidapp.ui.theme.Blue
import com.example.androidapp.ui.theme.DarkerPurple
import com.example.androidapp.ui.theme.Pink
import com.example.androidapp.ui.theme.Purple
import com.example.androidapp.ui.theme.darkerBlue
import com.example.androidapp.ui.theme.darkerPink

val RAINBOW_BACKGROUND = listOf(
    darkerPink,
    Color.hsv(30F, 1F, 1F),
    Color.hsv(60F, 1F, 1F),
    Color.hsv(120F, 1F, 1F),
    Color.hsv(180F, 1F, 1F),
    Color.hsv(240F, 1F, 1F),
    Color.hsv(270F, 1F, 1F),
    Pink
)

val DARK_MODE_BACKGROUND = listOf(
    DarkerPurple,
    Purple
)

val LIGHT_MODE_BACKGROUND = listOf(
    darkerBlue,
    Blue
)

const val ANIMATION_DURATION = 15000

@Composable
fun animatedBackground(isUniqrnTheme: Boolean, isDarkTheme: Boolean): Brush {
    val configuration = LocalConfiguration.current

    val density = LocalDensity.current;
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.roundToPx() }

    val infiniteTransition = rememberInfiniteTransition(label = "InfiniteTransitionForBackground")
    val animatedOffset = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = ANIMATION_DURATION, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "animatedOffset"
    )

    val gradientColors =
        if (isUniqrnTheme) {
            RAINBOW_BACKGROUND
        } else if (isDarkTheme) {
            DARK_MODE_BACKGROUND
        } else {
            LIGHT_MODE_BACKGROUND
        }

    return Brush.verticalGradient(
        colors = gradientColors,
        startY = animatedOffset.value * 1000 - screenHeightPx / 2,
        endY = animatedOffset.value * 1000 + screenHeightPx / 2
    )
}

@Composable
fun AddBackgroundToComposables(padding: PaddingValues, vararg composables: @Composable () -> Unit) {
    val mSettingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(SettingsRepository(LocalContext.current))
    )
    val isUniqrnTheme by mSettingsViewModel.isUniqrnModeEnabled.observeAsState(false)
    val isDarkTheme by mSettingsViewModel.isDarkTheme.observeAsState(false)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = animatedBackground(isUniqrnTheme, isDarkTheme)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            composables.forEach { composable ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                ) {
                    composable()
                }
            }
        }
    }
}

@Composable
fun HorizontalDivider() {
    Spacer(modifier = Modifier.height(8.dp))
    Divider(modifier = Modifier.fillMaxWidth())
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun Dialog(
    isShown: Boolean,
    title: String,
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (isShown) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(title) },
            text = { Text(text) },
            confirmButton = {
                Button(onClick = onConfirm) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Confirm")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }
        )
    }
}

@Composable
fun Toggle(
    toggleOption: Boolean,
    text: String,
    toggleOptionChange: (Boolean) -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = text)
            Switch(
                checked = toggleOption,
                onCheckedChange = { toggleOptionChange(it) })
        }
    }
}

@Composable
fun <T> DropDown(
    dropdownName: String,
    allOptions: Array<T>,
    valueFromOptionGetterFunction: (T) -> String,
    selectedValueModifier: T,
    onValueChange: (T) -> Unit
) {
    val mSettingsViewModel: SettingsViewModel =
        viewModel(factory = SettingsViewModelFactory(SettingsRepository(LocalContext.current)))
    var expanded by rememberSaveable { mutableStateOf(false) }
    val isDarkMode by mSettingsViewModel.isDarkTheme.observeAsState(false)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(0.dp, 12.dp, 0.dp, 8.dp),
            text = dropdownName,
            color = if (!isDarkMode) Color.Black else Color.Gray
        )

        TextButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 16.dp),
            onClick = { expanded = true }
        ) {
            Text(
                text = valueFromOptionGetterFunction(selectedValueModifier),
                color = if (!isDarkMode) Color.Black else Color.Gray
            )

            Icon(
                Icons.Default.MoreVert,
                contentDescription = "Localized description"
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .wrapContentWidth(Alignment.End),
            ) {
                allOptions.forEach { option ->
                    DropdownMenuItem(
                        modifier = Modifier.widthIn(min = 0.dp, max = 310.dp),
                        text = {
                            Text(
                                text = valueFromOptionGetterFunction(option),
                                color = if (!isDarkMode) Color.Black else Color.Gray
                            )
                        },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

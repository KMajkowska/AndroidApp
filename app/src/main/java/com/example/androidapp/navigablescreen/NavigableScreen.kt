package com.example.androidapp.navigablescreen

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

abstract class NavigableScreen : ComponentActivity() {
    @Composable
    fun ViewWithBackground() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.hsv(242F, 0.729F, 1F), Color.hsv(209F, 0.683F, 0.953F)),
                        startY = 0.0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxSize(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White,
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                    ) {
                        View()
                    }
                }
            }
        }
    }

    abstract val screenName: String

    abstract fun screenIcon(): ImageVector

    @Composable
    protected abstract fun View()

}

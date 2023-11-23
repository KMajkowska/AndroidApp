package com.example.androidapp.navigablescreen

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidapp.authorizationscreen.Login
import com.example.androidapp.database.viewmodel.DayViewModel

class AllNotes(private val mDayViewModel: DayViewModel) : NavigableScreen() {

    override val screenName: String
        get() = "All notes"

    override fun screenIcon(): ImageVector {
        return Icons.Default.AddCircle
    }

    @Composable
    override fun View() {
        val days = mDayViewModel.allDayEntitiesSortedByDate.observeAsState(initial = listOf()).value
        val context = LocalContext.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (days.isEmpty()) {
                Text(
                    text = "All Notes!",
                    style = TextStyle(fontSize = 24.sp)
                )

            }

            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                contentAlignment = Alignment.BottomEnd
            ) {
                IconButton(
                    onClick = {
                        val intent = Intent(context, CreateNote::class.java)
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .size(66.dp)
                        .shadow(2.dp, CircleShape)
                        .background(color = Color(0xFF3894ff), shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(20.dp)
                    )
                }
            }
        }
    }


}
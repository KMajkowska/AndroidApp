package com.example.androidapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.androidapp.database.AppDatabase
import com.example.androidapp.database.entity.NoteEntity
import com.example.androidapp.database.repository.NoteRepository
import com.example.androidapp.database.viewmodel.NoteViewModel
import com.example.androidapp.database.viewmodel.NoteViewModelFactory
import com.example.androidapp.navigablescreen.CustomBottomNavigation
import com.example.androidapp.ui.theme.AndroidAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("PreDB", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAA")

        val noteViewModel = NoteViewModelFactory(NoteRepository(AppDatabase.getDatabase(this).noteDao())).create(NoteViewModel::class.java)
        val notez = NoteEntity(0, "tytul", "AAAA")
        noteViewModel.insert(notez)
        Log.e("PreDB", "Note: ${notez.content}")

        setContent {
            AndroidAppTheme {
                CustomBottomNavigation()
            }
        }
    }
}

package com.example.androidapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton;
import android.content.Intent;


class AllNotes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_notes)

        val buttonClick = findViewById<ImageButton>(R.id.createNote)
        buttonClick.setOnClickListener {
            val intent = Intent(this, CreateNote::class.java)
            startActivity(intent)
        }
    }
}
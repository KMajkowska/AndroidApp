package com.example.androidapp.database.converter

import android.graphics.Color
import androidx.room.TypeConverter

class ColorConverter {
    @TypeConverter
    fun fromColor(color: Color): Int {
        return color.toArgb()
    }

    @TypeConverter
    fun toColor(colorInt: Int): Color {
        return Color.valueOf(colorInt)
    }
}
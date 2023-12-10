package com.example.androidapp.settings

import android.content.Context
import com.example.androidapp.R

enum class EventCategories(private val resourceId: Int) {
    GENERAL(R.string.event_category_general),
    PARTY(R.string.event_category_party),
    MEETING(R.string.event_category_meeting),
    SPORT(R.string.event_category_sport),
    FOOD(R.string.event_category_food),
    ENTERTAINMENT(R.string.event_category_entertainment);

    fun getDisplayName(context: Context): String {
        return context.getString(resourceId)
    }

    companion object {
        fun getAllDisplayNames(context: Context): List<String> {
            return values().map { it.getDisplayName(context) }
        }
    }
}
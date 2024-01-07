package com.example.androidapp.settings

import com.example.androidapp.R

enum class EventCategories(resourceId: Int) {

    GENERAL(R.string.event_category_general),
    PARTY(R.string.event_category_party),
    MEETING(R.string.event_category_meeting),
    SPORT(R.string.event_category_sport),
    FOOD(R.string.event_category_food),
    ENTERTAINMENT(R.string.event_category_entertainment);

    val resourceId = resourceId
}
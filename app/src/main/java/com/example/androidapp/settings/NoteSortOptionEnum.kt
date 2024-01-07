package com.example.androidapp.settings

import com.example.androidapp.R

enum class NoteSortOptionEnum(resourceId: Int) {
    ASCENDING(R.string.ascending),
    DESCENDING(R.string.descending);

    val resourceId = resourceId
}
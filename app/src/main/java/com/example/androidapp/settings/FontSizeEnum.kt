package com.example.androidapp.settings

import com.example.androidapp.R

enum class FontSizeEnum(resourceId: Int) {
    STANDARD(R.string.standard),
    SMALL(R.string.small),
    BIG(R.string.big);

    val resourceId = resourceId
}
package com.example.androidapp.settings

import com.example.androidapp.R

enum class SoundOptions(resourceId: Int) {
    PFUDON(R.string.PFUDON),
    BS(R.string.BS),
    CCDD(R.string.CCDD),
    NONE(R.string.NONE);

    val resourceId = resourceId
}
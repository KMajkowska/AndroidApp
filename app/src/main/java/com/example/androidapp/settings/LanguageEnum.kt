package com.example.androidapp.settings

enum class LanguageEnum(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    POLSKI("pl", "Polski");

    companion object {
        fun fromCode(code: String): LanguageEnum? {
            return values().find { it.code == code }
        }
    }
}
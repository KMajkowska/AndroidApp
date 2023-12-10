package com.example.androidapp.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

class LanguageChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent?.action == "com.example.androidapp.LANGUAGE_CHANGED") {
            // Pobierz preferencję języka
            val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
            val language = sharedPreferences.getString("My_Lang", "")

            // Zmiana języka
            setLocale(language, context)
        }
    }

    private fun setLocale(lang: String?, context: Context) {
        if (!lang.isNullOrBlank()) {
            val locale = Locale(lang)
            Locale.setDefault(locale)

            val resources = context.resources
            val configuration = Configuration(resources.configuration)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                configuration.setLocale(locale)
            } else {
                configuration.locale = locale
            }

            resources.updateConfiguration(configuration, resources.displayMetrics)
        }
    }
}

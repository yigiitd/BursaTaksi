package com.bursa.taksi.util

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Preferences
@Inject
constructor(@ApplicationContext context: Context) {

    companion object {
        private const val PREFERENCES_IS_SAVED = "preferences_saved"
    }

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun saveIsSaved() {
        sharedPreferences.edit(commit = true) {
            putBoolean(PREFERENCES_IS_SAVED, true)
        }
    }

    fun getIsSaved() = sharedPreferences?.getBoolean(PREFERENCES_IS_SAVED, false)
}
package com.nader.riyadalsalheen.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class AppPreferences(private val context: Context) {
    companion object {
        val LAST_HADITH_ID = intPreferencesKey("last_hadith_id")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
    }

}
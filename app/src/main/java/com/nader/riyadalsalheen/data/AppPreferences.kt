package com.nader.riyadalsalheen.data

import android.content.Context
import android.content.res.Configuration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AppPreferences(private val context: Context) {
    companion object {
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val FONT_SIZE = floatPreferencesKey("font_size")
        val BOOKMARKS = stringSetPreferencesKey("bookmarks")
        val READING_PROGRESS = intPreferencesKey("reading_progress")
    }

    // Save dark theme preference
    suspend fun saveDarkTheme(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_THEME] = isDark
        }
    }

    // Get dark theme preference
    val isDarkTheme: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DARK_THEME] ?: isSystemInDarkTheme(context)
        }

    // Save font size
    suspend fun saveFontSize(size: Float) {
        context.dataStore.edit { preferences ->
            preferences[FONT_SIZE] = size
        }
    }

    // Get font size
    val fontSize: Flow<Float> = context.dataStore.data
        .map { preferences ->
            preferences[FONT_SIZE] ?: 18f
        }

    suspend fun toggleBookmark(hadithId: Int) {
        context.dataStore.edit { preferences ->
            val currentBookmarks = preferences[BOOKMARKS] ?: emptySet()
            if(currentBookmarks.contains(hadithId.toString())) {
                preferences[BOOKMARKS] = currentBookmarks - hadithId.toString()
            }
            else {
                preferences[BOOKMARKS] = currentBookmarks + hadithId.toString()
            }
        }
    }

    // Get bookmarks
    val bookmarks: Flow<Set<String>> = context.dataStore.data
        .map { preferences ->
            preferences[BOOKMARKS] ?: emptySet()
        }

    // Save reading progress
    suspend fun saveReadingProgress(hadithId: Int) {
        context.dataStore.edit { preferences ->
            preferences[READING_PROGRESS] = hadithId
        }
    }

    // Get reading progress
    val readingProgress: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[READING_PROGRESS] ?: 1
        }

    // Clear all preferences
    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

// Helper function to check system dark theme outside Composable
private fun isSystemInDarkTheme(context: Context): Boolean {
    val uiMode = context.resources.configuration.uiMode
    return (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
}
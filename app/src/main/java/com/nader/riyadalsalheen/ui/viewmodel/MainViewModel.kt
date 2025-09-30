package com.nader.riyadalsalheen.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nader.riyadalsalheen.data.AppPreferences
import com.nader.riyadalsalheen.data.repository.RiyadSalheenRepository
import com.nader.riyadalsalheen.model.Book
import com.nader.riyadalsalheen.model.Door
import com.nader.riyadalsalheen.model.Hadith
import com.nader.riyadalsalheen.model.HadithDetails
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RiyadSalheenRepository(application)
    private val preferences = AppPreferences(application)
    val packageInfo = application.packageManager.getPackageInfo(application.packageName, 0)

    // UI States
    var books = emptyList<Book>()
    var doors = emptyList<Door>()
    var hadithCount = 0

    val cachedHadiths: MutableMap<Int, HadithDetails> = mutableMapOf()

    var currentHadithId = 0
    val searchResults = mutableStateOf(emptyList<Hadith>())
    val systemTheme = mutableStateOf(true)
    val fontSize = mutableFloatStateOf(18f)
    val bookmarks = mutableStateOf(emptyList<Hadith>())

    var isDarkMode = false

    // Loading states
    val isInitialDataLoaded = mutableStateOf(false)
    val isSearching = mutableStateOf(false)


    init {
        viewModelScope.launch {
            books = repository.getAllBooks()
            doors = repository.getAllDoors()
            hadithCount = repository.getHadithsCount()

            fontSize.floatValue = preferences.fontSize.first()
            systemTheme.value = preferences.systemTheme.first()
            bookmarks.value = preferences.bookmarks
                .first()
                .mapNotNull { it.toIntOrNull() }
                .let { repository.getHadithsByIds(it) }
            currentHadithId = preferences.readingProgress.first()
            updateCachedHadiths()

            isInitialDataLoaded.value = true
        }
    }

    private fun loadHadithDetails(hadithId: Int): HadithDetails? {
        if(hadithId < 1 || hadithId > hadithCount) {
            return null
        }

        if (cachedHadiths.containsKey(hadithId)) {
            return cachedHadiths[hadithId]
        }

        val hadith = repository.getHadithById(hadithId)
        if (hadith != null) {
            val newHadith = HadithDetails(
                hadith = hadith,
                door = doors[hadith.doorId - 1], // convert door/book id from 1 based to zero based
                book = books[hadith.bookId - 1]
            )
            cachedHadiths[hadithId] = newHadith
            return newHadith
        }
        return null
    }

    private fun updateCachedHadiths() {
        val currentId = currentHadithId
        val minId = (currentId - 3).coerceIn(1, hadithCount)
        val maxId = (currentId + 3).coerceIn(1, hadithCount)

        // Remove unneeded items first for efficiency
        cachedHadiths.entries.removeIf { kotlin.math.abs(it.key - currentId) > 3 }

        // Use a more idiomatic way to add missing items
        (minId..maxId).forEach { idx ->
            loadHadithDetails(idx)
        }
    }

    fun navigateToHadith(hadithId: Int) {
        if(currentHadithId != hadithId) {
            currentHadithId = hadithId
            viewModelScope.launch {
                updateCachedHadiths()
                preferences.saveReadingProgress(hadithId)
            }
        }
    }

    fun getFirstHadithIdInDoor(doorId: Int): Int?{
        return repository.getFirstHadithIdInDoor(doorId)
    }

    fun searchHadiths(query: String) {
        if (query.isBlank() && query.length >= 3) {
            searchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            isSearching.value = true
            searchResults.value = repository.searchHadiths(query.trim())
            isSearching.value = false
        }
    }

    fun toggleBookmark(hadithId: Int) {
        viewModelScope.launch {
            preferences.toggleBookmark(hadithId)

            val initialBookmarks = preferences.bookmarks.first()
            val bookmarkIds = initialBookmarks.mapNotNull { it.toIntOrNull() }
            bookmarks.value = repository.getHadithsByIds(bookmarkIds)
        }
    }

    fun toggleSystemTheme() {
        viewModelScope.launch {
            systemTheme.value = !systemTheme.value
            preferences.saveSystemTheme(systemTheme.value)
        }
    }

    fun updateFontSize(size: Float) {
        viewModelScope.launch {
            fontSize.floatValue = size.coerceIn(14f, 30f)
            preferences.saveFontSize(fontSize.floatValue)
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.close()
    }

}
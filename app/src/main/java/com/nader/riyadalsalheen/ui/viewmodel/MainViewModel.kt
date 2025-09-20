package com.nader.riyadalsalheen.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nader.riyadalsalheen.data.AppPreferences
import com.nader.riyadalsalheen.data.repository.RiyadSalheenRepository
import com.nader.riyadalsalheen.model.Book
import com.nader.riyadalsalheen.model.Door
import com.nader.riyadalsalheen.model.Hadith
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class HadithDetails(val hadith: Hadith, val door: Door, val book: Book)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RiyadSalheenRepository(application)
    private val preferences = AppPreferences(application)
    val packageInfo = application.packageManager.getPackageInfo(application.packageName, 0)

    // UI States
    val books = mutableStateOf(emptyList<Book>())


    val hadithCount = mutableIntStateOf(0)
    val searchResults = mutableStateOf(emptyList<Hadith>())
    val isDarkTheme = mutableStateOf(false)
    val fontSize = mutableFloatStateOf(18f)
    val bookmarks = mutableStateOf(emptyList<Hadith>())

    // Current navigation state
    val currentBookDoors = mutableStateOf(emptyList<Door>())
    val currentDoorHadiths = mutableStateOf(emptyList<Hadith>())
    val currentHadith = mutableStateOf<HadithDetails?>(null)

    // Loading states
    val isLoading = mutableStateOf(false)
    val isSearching = mutableStateOf(false)


    init {
        viewModelScope.launch {
            isLoading.value = true

            hadithCount.intValue = repository.getHadithsCount()

            books.value = repository.getAllBooks()

            fontSize.floatValue = preferences.fontSize.first()
            isDarkTheme.value = preferences.isDarkTheme.first()

            val initialBookmarks = preferences.bookmarks.first()
            val bookmarkIds = initialBookmarks.mapNotNull { it.toIntOrNull() }
            bookmarks.value = repository.getHadithsByIds(bookmarkIds)

            val savedProgress = preferences.readingProgress.first()
            navigateToHadith(savedProgress)

            isLoading.value = false
        }
    }

    // Navigation functions
    fun navigateToBook(bookId: Int) {
        viewModelScope.launch {
            isLoading.value = true
            currentBookDoors.value = repository.getDoorsByBook(bookId)
            isLoading.value = false
        }
    }

    fun navigateToDoor(doorId: Int) {
        viewModelScope.launch {
            isLoading.value = true
            currentDoorHadiths.value = repository.getHadithsByDoor(doorId)
            isLoading.value = false
        }
    }

    fun navigateToHadith(hadithId: Int) {
        if(
            hadithId < 1 ||
            hadithId > hadithCount.intValue ||
            hadithId == currentHadith.value?.hadith?.id
        ) {
            return
        }

        viewModelScope.launch {
            isLoading.value = true
            val hadith = repository.getHadithById(hadithId)
            if (hadith != null) {
                currentBookDoors.value = repository.getDoorsByBook(hadith.bookId)
                currentDoorHadiths.value = repository.getHadithsByDoor(hadith.doorId)

                val door = currentBookDoors.value.find { it.id == hadith.doorId }
                val book = books.value.find { it.id == hadith.bookId }
                if(door != null && book != null) {
                    currentHadith.value = HadithDetails(
                        hadith = hadith,
                        door = door,
                        book = book
                    )
                }
                preferences.saveReadingProgress(hadith.id)
            }
            isLoading.value = false
        }
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

    fun toggleDarkTheme() {
        viewModelScope.launch {
            isDarkTheme.value = !isDarkTheme.value
            preferences.saveDarkTheme(!isDarkTheme.value)
        }
    }

    fun updateFontSize(size: Float) {
        viewModelScope.launch {
            fontSize.floatValue = size
            preferences.saveFontSize(fontSize.floatValue)
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.close()
    }

}
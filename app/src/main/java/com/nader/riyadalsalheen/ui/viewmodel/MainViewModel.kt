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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RiyadSalheenRepository(application)
    private val preferences = AppPreferences(application)
    val packageInfo = application.packageManager.getPackageInfo(application.packageName, 0)

    // UI States
    val books = mutableStateOf(emptyList<Book>())
    val doors = mutableStateOf(emptyList<Door>())
    val hadiths = mutableStateOf(emptyList<Hadith>())
    val hadith = mutableStateOf<Hadith?>(null)
    val hadithCount = mutableIntStateOf(0)
    val searchResults = mutableStateOf(emptyList<Hadith>())
    val isDarkTheme = mutableStateOf(false)
    val fontSize = mutableFloatStateOf(18f)
    val bookmarks = mutableStateOf(emptyList<Hadith>())
    val lastHadithId = mutableIntStateOf(0)

    // Current navigation state
    val currentBook = mutableStateOf<Book?>(null)
    val currentDoor = mutableStateOf<Door?>(null)
    // Loading states
    val isLoading = mutableStateOf(false)
    val isSearching = mutableStateOf(false)


    init {
        viewModelScope.launch {
            hadithCount.intValue = repository.getHadithsCount()
            loadBooks()

            preferences.isDarkTheme.collect { darkTheme ->
                isDarkTheme.value = darkTheme
            }

            preferences.bookmarks.collect { bookmarkSet ->
                val bookmarkIds = bookmarkSet.mapNotNull { it.toIntOrNull() }
                bookmarks.value = repository.getHadithsByIds(bookmarkIds)
            }

            // Observe font size
            preferences.fontSize.collect { size ->
                fontSize.floatValue = size
            }

            preferences.readingProgress.collect { id ->
                lastHadithId.intValue = id
            }
        }
    }

    fun loadBooks() {
        viewModelScope.launch {
            isLoading.value = true
            books.value = repository.getAllBooks()
            isLoading.value = false
        }
    }

    fun loadDoors(bookId: Int) {
        viewModelScope.launch {
            isLoading.value = true
            doors.value = repository.getDoorsByBook(bookId)
            currentBook.value = repository.getBookById(bookId)
            isLoading.value = false
        }
    }

    fun loadHadiths(doorId: Int) {
        viewModelScope.launch {
            isLoading.value = true
            hadiths.value = repository.getHadithsByDoor(doorId)
            currentDoor.value = repository.getDoorById(doorId)
            isLoading.value = false
        }
    }

    fun loadHadith(hadithId: Int) {
        viewModelScope.launch {
            isLoading.value = true
            val currentHadith = repository.getHadithById(hadithId)
            if (currentHadith != null) {
                hadith.value = currentHadith
                currentBook.value = repository.getBookById(currentHadith.bookId)
                currentDoor.value = repository.getDoorById(currentHadith.doorId)
                doors.value = repository.getDoorsByBook(currentHadith.bookId)
                hadiths.value = repository.getHadithsByDoor(currentHadith.doorId)
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
        }
    }

    fun isBookmarked(hadithId: Int): Boolean {
        return bookmarks.value.any{ it.id == hadithId }
    }

    fun toggleDarkTheme() {
        viewModelScope.launch {
            val currentTheme = isDarkTheme.value
            preferences.saveDarkTheme(!currentTheme)
        }
    }

    fun updateFontSize(size: Float) {
        viewModelScope.launch {
            preferences.saveFontSize(size)
        }
    }

    fun saveReadingProgress(hadithId: Int) {
        viewModelScope.launch {
            preferences.saveReadingProgress(hadithId)
        }
    }

    fun getRandomHadith(): Hadith? {
        return repository.getRandomHadith()
    }

    fun getNextHadithId(currentId: Int): Int {
        return (currentId + 1).coerceIn(1, hadithCount.intValue)
    }

    fun getPreviousHadithId(currentId: Int): Int {
        return (currentId - 1).coerceIn(1, hadithCount.intValue)
    }

    override fun onCleared() {
        super.onCleared()
        repository.close()
    }

}
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
    val cachedHadiths: MutableMap<Int, HadithDetails> = mutableMapOf()

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
            currentBookDoors.value = repository.getDoorsByBook(bookId)
        }
    }

    fun navigateToDoor(doorId: Int) {
        viewModelScope.launch {
            currentDoorHadiths.value = repository.getHadithsByDoor(doorId)
        }
    }

    private fun loadHadithDetails(hadithId: Int): HadithDetails? {
        if(hadithId < 1 || hadithId > hadithCount.intValue) {
            return null
        }

        val hadith = repository.getHadithById(hadithId)
        if (hadith != null) {
            val door = repository.getDoorById(doorId = hadith.doorId)
            val book = books.value.find { it.id == hadith.bookId }
            if(door != null && book != null) {
                return HadithDetails(
                    hadith = hadith,
                    door = door,
                    book = book
                )
            }
        }
        return null
    }

    private fun updateCachedHadiths() {
        val hadithDetail = currentHadith.value ?: return

        val currentId = hadithDetail.hadith.id
        val minId = (currentId - 3).coerceIn(1, hadithCount.intValue)
        val maxId = (currentId + 3).coerceIn(1, hadithCount.intValue)

        // Remove unneeded items first for efficiency
        cachedHadiths.entries.removeIf { kotlin.math.abs(it.key - currentId) > 3 }

        // Use a more idiomatic way to add missing items
        (minId..maxId).forEach { idx ->
            if (!cachedHadiths.containsKey(idx)) {
                loadHadithDetails(idx)?.let { cachedHadiths[idx] = it }
            }
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

        val onHadithDetailsLoaded: (HadithDetails) -> Unit = { hadithDetails ->
            viewModelScope.launch {
                currentBookDoors.value = repository.getDoorsByBook(hadithDetails.book.id)
                currentDoorHadiths.value = repository.getHadithsByDoor(hadithDetails.door.id)
                updateCachedHadiths()
                preferences.saveReadingProgress(hadithDetails.hadith.id)
            }
        }

        if(cachedHadiths.contains(hadithId)) {
            cachedHadiths[hadithId]?.let {
                currentHadith.value = it
                onHadithDetailsLoaded(it)
            }
        }
        else {
            viewModelScope.launch {
                loadHadithDetails(hadithId)?.let {
                    currentHadith.value = it
                    onHadithDetailsLoaded(it)
                }
            }
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
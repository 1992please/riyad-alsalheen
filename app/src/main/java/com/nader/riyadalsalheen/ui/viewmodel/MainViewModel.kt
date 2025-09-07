package com.nader.riyadalsalheen.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

import com.nader.riyadalsalheen.data.repository.RiyadSalheenRepository
import com.nader.riyadalsalheen.model.Book
import com.nader.riyadalsalheen.model.Door
import com.nader.riyadalsalheen.model.Hadith
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RiyadSalheenRepository(application)
    val packageInfo = application.packageManager.getPackageInfo(application.packageName, 0)
    val books = mutableStateOf(emptyList<Book>())
    val doors = mutableStateOf(emptyList<Door>())
    val hadiths = mutableStateOf(emptyList<Hadith>())
    val hadith = mutableStateOf<Hadith?>(null)
    val hadithCount = mutableStateOf(0)
    init {
        viewModelScope.launch {
            hadithCount.value = repository.getHadithsCount()
        }
    }

    fun loadBooks() {
        viewModelScope.launch {
            books.value = repository.getAllBooks()
        }
    }

    fun loadDoors(bookId: Int) {
        viewModelScope.launch {
            doors.value = repository.getDoorsByBook(bookId)
        }
    }

    fun loadHadiths(doorId: Int) {
        viewModelScope.launch {
            hadiths.value = repository.getHadithsByDoor(doorId)
        }
    }

    fun loadHadith(hadithId: Int) {
        hadith.value = hadiths.value.find { it.id == hadithId }
        if(hadith.value == null) {
            viewModelScope.launch {
                val currentHadith = repository.getHadithById(hadithId);
                if(currentHadith != null) {
                    hadith.value = currentHadith
                    doors.value = repository.getDoorsByBook(currentHadith.bookId)
                    hadiths.value = repository.getHadithsByDoor(currentHadith.doorId)
                }
            }
        }
    }
}
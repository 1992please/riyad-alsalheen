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

    val books = mutableStateOf(emptyList<Book>())
    val doors = mutableStateOf(emptyList<Door>())
    val hadiths = mutableStateOf(emptyList<Hadith>())


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

}
package com.nader.riyadalsalheen

import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nader.riyadalsalheen.data.repository.RiyadSalheenRepository

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class RepositoryTest {

    private lateinit var repository: RiyadSalheenRepository

    companion object {
        private const val TAG = "RepositoryTest"
    }

    @Before
    fun setup() {
        Log.d(TAG, "Setting up test...")
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        repository = RiyadSalheenRepository(context)
        Log.d(TAG, "Repository initialized")
    }

    @Test
    fun testGetAllBooks_returnsNonEmptyList() {
        Log.d(TAG, "Starting test: testGetAllBooks_returnsNonEmptyList")

        // When
        val books = repository.getAllBooks()
        Log.d(TAG, "Retrieved ${books.size} books")

        // Then
        assertTrue("Books list should not be empty", books.isNotEmpty())
        Log.d(TAG, "Test passed: books list is not empty")

        // Log book details for debugging
        books.forEachIndexed { index, book ->
            Log.d(TAG, "Book $index: ID=${book.id}, Title='${book.title}'")
        }
    }
}
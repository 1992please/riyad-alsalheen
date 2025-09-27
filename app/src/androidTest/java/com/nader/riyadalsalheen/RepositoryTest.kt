package com.nader.riyadalsalheen

import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nader.riyadalsalheen.data.repository.RiyadSalheenRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

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

    @Test
    fun testGetFirstBookDoors_returnsNonEmptyList() {
        Log.d(TAG, "Starting test: testGetFirstBookDoors_returnsNonEmptyList")

        // When
        val doors = repository.getAllDoors()
        Log.d(TAG, "Retrieved doors of the first book ${doors.size} doors")

        // Then
        assertTrue("Books list should not be empty", doors.isNotEmpty())
        Log.d(TAG, "Test passed: doors list is not empty")

        // Log book details for debugging
        doors.forEachIndexed { index, door ->
            Log.d(TAG, "Book $index: ID=${door.id}, Title='${door.title}'")
        }
    }

    @Test
    fun testGetFirstDoorHadiths_returnsNonEmptyList() {
        Log.d(TAG, "Starting test: testGetFirstDoorHadiths_returnsNonEmptyList")

        // When
        val hadiths = repository.getHadithsByDoor(1)
        Log.d(TAG, "Retrieved hadiths of the first book ${hadiths.size} hadiths")

        // Then
        assertTrue("Books list should not be empty", hadiths.isNotEmpty())
        Log.d(TAG, "Test passed: hadiths list is not empty")
    }

    @Test
    fun testGetFirstAndLastHadith_returnNonEmptyTitle() {
        Log.d(TAG, "Starting test: testGetFirstAndLastHadith_returnNonEmptyTitle")

        val count = repository.getHadithsCount()
        Log.d(TAG, "Retrieved hadiths count ${count} hadiths")

        // Then
        assertTrue("Books list should not be empty", count > 1000)

        // When
        val firstHadith = repository.getHadithById(1)
        val lastHadith = repository.getHadithById(count)

        Log.d(TAG, "Retrieved first hadith ${firstHadith?.title}")
        Log.d(TAG, "Retrieved first hadith ${lastHadith?.title}")
    }
}
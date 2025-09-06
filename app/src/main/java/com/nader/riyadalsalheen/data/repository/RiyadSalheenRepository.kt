package com.nader.riyadalsalheen.data.repository

import android.content.Context
import com.nader.riyadalsalheen.data.DatabaseHelper
import com.nader.riyadalsalheen.model.Book
import com.nader.riyadalsalheen.model.Door
import com.nader.riyadalsalheen.model.Hadith


class RiyadSalheenRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)
    private val database by lazy { dbHelper.readableDatabase }
    companion object {
        private const val TABLE_BOOKS = "books"
        private const val TABLE_DOORS = "doors"
        private const val TABLE_HADITHS = "hadiths"

        private const val COLUMN_ID = "id"
        private const val COLUMN_BOOK_ID = "book_id"
        private const val COLUMN_DOOR_ID = "door_id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_HADITH = "hadith"
        private const val COLUMN_SHARH = "sharh"
    }

    fun getAllBooks(): List<Book> {
        val books = mutableListOf<Book>()
        val cursor = database.query(TABLE_BOOKS, null, null, null, null, null, null)

        cursor.use {
            while (it.moveToNext()) {
                books.add(Book(
                    id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)),
                    title = it.getString(it.getColumnIndexOrThrow(COLUMN_TITLE))
                ))
            }
        }
        return books
    }

    fun getDoorsByBook(bookId: Int): List<Door> {
        val doors = mutableListOf<Door>()
        val selection = "$COLUMN_BOOK_ID = ?"
        val selectionArgs = arrayOf(bookId.toString())

        val cursor = database.query(TABLE_DOORS, null, selection, selectionArgs, null, null, null)

        cursor.use {
            while (it.moveToNext()) {
                doors.add(Door(
                    id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)),
                    bookId = it.getInt(it.getColumnIndexOrThrow(COLUMN_BOOK_ID)),
                    title = it.getString(it.getColumnIndexOrThrow(COLUMN_TITLE))
                ))
            }
        }
        return doors
    }

    fun getHadithsByDoor(doorId: Int): List<Hadith> {
        val hadiths = mutableListOf<Hadith>()
        val selection = "$COLUMN_DOOR_ID = ?"
        val selectionArgs = arrayOf(doorId.toString())

        // Order by ID for proper sequencing
        val orderBy = "$COLUMN_ID ASC"

        val cursor = database.query(TABLE_HADITHS, null, selection, selectionArgs, null, null, orderBy)

        cursor.use {
            while (it.moveToNext()) {
                hadiths.add(Hadith(
                    id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)),
                    doorId = it.getInt(it.getColumnIndexOrThrow(COLUMN_DOOR_ID)),
                    bookId = it.getInt(it.getColumnIndexOrThrow(COLUMN_BOOK_ID)),
                    title = it.getString(it.getColumnIndexOrThrow(COLUMN_TITLE)),
                    hadith = it.getString(it.getColumnIndexOrThrow(COLUMN_HADITH)),
                    sharh = it.getString(it.getColumnIndexOrThrow(COLUMN_SHARH))
                ))
            }
        }
        return hadiths
    }

    fun getHadithById(hadithId: Int): Hadith? {
        val selection = "$COLUMN_ID = ?"
        val selectionArgs = arrayOf(hadithId.toString())

        val cursor = database.query(
            TABLE_HADITHS, null,  selection, selectionArgs, null, null, null)

        cursor.use {
            if (it.moveToFirst()) {
                return Hadith(
                    id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)),
                    doorId = it.getInt(it.getColumnIndexOrThrow(COLUMN_DOOR_ID)),
                    bookId = it.getInt(it.getColumnIndexOrThrow(COLUMN_BOOK_ID)),
                    title = it.getString(it.getColumnIndexOrThrow(COLUMN_TITLE)) ?: "",
                    hadith = it.getString(it.getColumnIndexOrThrow(COLUMN_HADITH)),
                    sharh = it.getString(it.getColumnIndexOrThrow(COLUMN_SHARH)) ?: ""
                )
            }
        }
        return null
    }
}
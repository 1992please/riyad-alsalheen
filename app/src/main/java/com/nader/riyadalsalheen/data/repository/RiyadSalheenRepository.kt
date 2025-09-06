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
        val query = "SELECT * FROM $TABLE_BOOKS ORDER BY $COLUMN_ID ASC"
        database.rawQuery(query, null).use { cursor ->
            while (cursor.moveToNext()) {
                books.add(Book(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
                ))
            }
        }
        return books
    }

    fun getDoorsByBook(bookId: Int): List<Door> {
        val doors = mutableListOf<Door>()
        val query = "SELECT * FROM $TABLE_DOORS WHERE $COLUMN_BOOK_ID = ? ORDER BY $COLUMN_ID ASC"
        val selectionArgs = arrayOf(bookId.toString())

        database.rawQuery(query, selectionArgs).use { cursor ->
            while (cursor.moveToNext()) {
                doors.add(Door(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    bookId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BOOK_ID)),
                    title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
                ))
            }
        }
        return doors
    }

    fun getHadithsByDoor(doorId: Int): List<Hadith> {
        val hadiths = mutableListOf<Hadith>()
        val query = "SELECT * FROM $TABLE_HADITHS WHERE $COLUMN_DOOR_ID = ? ORDER BY $COLUMN_ID ASC"
        val selectionArgs = arrayOf(doorId.toString())

        database.rawQuery(query, selectionArgs).use { cursor ->
            while (cursor.moveToNext()) {
                hadiths.add(Hadith(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    doorId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DOOR_ID)),
                    bookId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BOOK_ID)),
                    title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                    hadith = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HADITH)),
                    sharh = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SHARH))
                ))
            }
        }
        return hadiths
    }

    fun getHadithById(hadithId: Int): Hadith? {
        val query = "SELECT * FROM $TABLE_HADITHS WHERE $COLUMN_ID = ?"
        val selectionArgs = arrayOf(hadithId.toString())

        database.rawQuery(query, selectionArgs).use { cursor ->
            if (cursor.moveToFirst()) {
                return Hadith(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    doorId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DOOR_ID)),
                    bookId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BOOK_ID)),
                    title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)) ?: "",
                    hadith = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HADITH)),
                    sharh = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SHARH)) ?: ""
                )
            }
        }
        return null
    }

    fun getHadithsCount(): Int {
        val query = "SELECT COUNT(*) FROM $TABLE_HADITHS"
        database.rawQuery(query, null).use { cursor ->
            if (cursor.moveToFirst()) {
                return cursor.getInt(0) // Returns the count from the first column
            }
        }
        return 0
    }
}
package com.nader.riyadalsalheen.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.FileOutputStream
import java.io.IOException

class DatabaseHelper(context: Context)
    : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "riyad_salheen.db"
        private const val DATABASE_VERSION = 1
    }

    private val databasePath by lazy {
        context.getDatabasePath(DATABASE_NAME).path;
    }

    init{
        if (!checkDatabase()) {
            try {
                copyDatabase(context)
            } catch (e: IOException) {
                throw Error("Error copying database: ${e.message}")
            }
        }
    }

    private fun checkDatabase(): Boolean {
        return try {
            SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READONLY)
            true
        } catch (e: Exception) {
            false
        }
    }

    @Throws(IOException::class)
    private fun copyDatabase(context: Context) {
        val input = context.assets.open("databases/$DATABASE_NAME")
        val outputFile = context.getDatabasePath(DATABASE_NAME)
        outputFile.parentFile?.mkdirs()

        FileOutputStream(outputFile).use { output ->
            val buffer = ByteArray(1024)
            var length: Int
            while (input.read(buffer).also { length = it } > 0) {
                output.write(buffer, 0, length)
            }
            output.flush()
        }
        input.close()
    }

    override fun getReadableDatabase(): SQLiteDatabase {
        return SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READONLY)
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Database is copied from assets
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades
    }
}
package com.mobnews.app.DataBase

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.mobnews.app.DataClass.notificationDataClass

class NotificationDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "NotificationDB.db"
        private const val SQL_CREATE_ENTRIES = """
            CREATE TABLE ${NotificationContract.NotificationEntry.TABLE_NAME} (
                ${NotificationContract.NotificationEntry.COLUMN_NAME_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${NotificationContract.NotificationEntry.COLUMN_NAME_TITLE} TEXT,
                ${NotificationContract.NotificationEntry.COLUMN_NAME_SUBTITLE} TEXT,
                ${NotificationContract.NotificationEntry.COLUMN_NAME_DATE} TEXT)
            """
        private const val SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS ${NotificationContract.NotificationEntry.TABLE_NAME}"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    fun insertNotification(title: String, body: String,date:String) {
        val db = writableDatabase

        val values = ContentValues().apply {
            put(NotificationContract.NotificationEntry.COLUMN_NAME_TITLE, title)
            put(NotificationContract.NotificationEntry.COLUMN_NAME_SUBTITLE, body)
            put(NotificationContract.NotificationEntry.COLUMN_NAME_DATE, System.currentTimeMillis().toString())
        }

        db.insert(NotificationContract.NotificationEntry.TABLE_NAME, null, values)
        db.close() // Close the database connection after insertion
    }

    fun getAllNotifications(): ArrayList<notificationDataClass> {
        val notificationsList = ArrayList<notificationDataClass>()
        val db = readableDatabase

        val projection = arrayOf(
            NotificationContract.NotificationEntry.COLUMN_NAME_TITLE,
            NotificationContract.NotificationEntry.COLUMN_NAME_SUBTITLE,
            NotificationContract.NotificationEntry.COLUMN_NAME_DATE
        )

        val sortOrder = "${NotificationContract.NotificationEntry.COLUMN_NAME_DATE} DESC"

        val cursor = db.query(
            NotificationContract.NotificationEntry.TABLE_NAME,  // The table to query
            projection,                                         // The array of columns to return (null for all)
            null,                                               // The columns for the WHERE clause
            null,                                               // The values for the WHERE clause
            null,                                               // don't group the rows
            null,                                               // don't filter by row groups
            sortOrder                                           // The sort order
        )

        cursor.use { cursor ->
            while (cursor.moveToNext()) {
                val titleIndex = cursor.getColumnIndex(NotificationContract.NotificationEntry.COLUMN_NAME_TITLE)
                val bodyIndex = cursor.getColumnIndex(NotificationContract.NotificationEntry.COLUMN_NAME_SUBTITLE)
                val timestampIndex = cursor.getColumnIndex(NotificationContract.NotificationEntry.COLUMN_NAME_DATE)

                // Check if column indices are valid
                if (titleIndex != -1 && bodyIndex != -1 && timestampIndex != -1) {
                    val title = cursor.getString(titleIndex)
                    val body = cursor.getString(bodyIndex)
                    val timestamp = cursor.getString(timestampIndex)

                    val notification = notificationDataClass(title, body, timestamp)
                    notificationsList.add(notification)
                } else {
                    // Handle the case where one or more columns do not exist
                    // Log an error or perform appropriate error handling
                    Log.e("NotificationDbHelper", "Invalid column index found in cursor")
                }
            }
        }

        cursor.close() // Close cursor after use
        db.close() // Close database after use

        return notificationsList
    }
}

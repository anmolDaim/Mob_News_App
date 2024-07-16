package com.mobnews.app.DataBase

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.mobnews.app.DataBase.NotificationContract.NotificationEntry.TABLE_NAME
import com.mobnews.app.DataClass.notificationDataClass

class NotificationDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION = 4
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

    init {
        val dbPath = context.getDatabasePath(DATABASE_NAME).absolutePath
        Log.d("NotificationDbHelper", "Database path: $dbPath")
    }

    override fun onCreate(db: SQLiteDatabase) {
        try {
            db.execSQL(SQL_CREATE_ENTRIES)
            Log.d("NotificationDbHelper", "Database created with table: ${NotificationContract.NotificationEntry.TABLE_NAME}")
        } catch (e: Exception) {
            Log.e("NotificationDbHelper", "Error creating database: ${e.message}")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        try {
            db.execSQL(SQL_DELETE_ENTRIES)
            onCreate(db)
            Log.d("NotificationDbHelper", "Database upgraded to version $newVersion")
        } catch (e: Exception) {
            Log.e("NotificationDbHelper", "Error upgrading database: ${e.message}")
        }
    }

    fun insertNotification(title: String, body: String, date: String) {
        val db = writableDatabase
        try {
            val values = ContentValues().apply {
                put(NotificationContract.NotificationEntry.COLUMN_NAME_TITLE, title)
                put(NotificationContract.NotificationEntry.COLUMN_NAME_SUBTITLE, body)
                put(NotificationContract.NotificationEntry.COLUMN_NAME_DATE, date)
            }

            val newRowId = db.insert(NotificationContract.NotificationEntry.TABLE_NAME, null, values)
            Log.d("NotificationDbHelper", "Inserted new row with ID: $newRowId")
        } catch (e: Exception) {
            Log.e("NotificationDbHelper", "Error inserting notification: ${e.message}")
        } finally {
            db.close()
        }
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
            NotificationContract.NotificationEntry.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            sortOrder
        )

        cursor.use { cur ->
            while (cur.moveToNext()) {
                val titleIndex = cur.getColumnIndex(NotificationContract.NotificationEntry.COLUMN_NAME_TITLE)
                val bodyIndex = cur.getColumnIndex(NotificationContract.NotificationEntry.COLUMN_NAME_SUBTITLE)
                val timestampIndex = cur.getColumnIndex(NotificationContract.NotificationEntry.COLUMN_NAME_DATE)

                if (titleIndex != -1 && bodyIndex != -1 && timestampIndex != -1) {
                    val title = cur.getString(titleIndex)
                    val body = cur.getString(bodyIndex)
                    val notification = notificationDataClass(title, body)
                    notificationsList.add(notification)
                } else {
                    Log.e("NotificationDbHelper", "Invalid column index found in cursor")
                }
            }
        }

        cursor.close()
        db.close()

        return notificationsList
    }
//    fun deleteAllNotifications() {
//        val db = writableDatabase
//        db.delete(TABLE_NAME, null, null)
//    }
}

package com.mobnews.app.DataBase

import android.provider.BaseColumns

object NotificationContract {
    object NotificationEntry : BaseColumns {
        const val TABLE_NAME = "notification"
        const val COLUMN_NAME_ID = "id"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_SUBTITLE = "subtitle"
        const val COLUMN_NAME_DATE = "date"
    }
}
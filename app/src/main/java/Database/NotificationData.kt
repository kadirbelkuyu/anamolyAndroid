package Database

import Models.NotificationModel
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.lifecycle.LiveData

/**
 * Created on 04-02-2020.
 */
class NotificationData(val context: Context) {

    private var db: SQLiteDatabase? = null
    private val dbHandler: DatabaseHandler

    companion object {

        const val NOTIFICATION_TABLE = "notification_table"

        const val COLUMN_NOTI_ID = "noti_id"
        const val COLUMN_PLAY_ID = "noti_play_id"
        const val COLUMN_TITLE = "noti_title"
        const val COLUMN_MESSAGE = "noti_message"
        const val COLUMN_TYPE = "noti_type"
        const val COLUMN_REF_ID = "noti_ref_id"

    }

    init {
        dbHandler = DatabaseHandler(context)
    }

    fun setNotification(notificationModel: NotificationModel): String {
        db = dbHandler.writableDatabase

        val values = ContentValues()
        values.put(COLUMN_PLAY_ID, notificationModel.noti_play_id)
        values.put(COLUMN_TITLE, notificationModel.noti_title)
        values.put(COLUMN_MESSAGE, notificationModel.noti_message)
        values.put(COLUMN_TYPE, notificationModel.noti_type)
        values.put(COLUMN_REF_ID, notificationModel.noti_ref_id)

        if (isInNotification(notificationModel.noti_id)) {
            val id = db!!.update(
                NOTIFICATION_TABLE,
                values,
                COLUMN_PLAY_ID + "=" + notificationModel.noti_play_id,
                null
            )
            db!!.close()
            return id.toString()
        } else {
            val id = db!!.insert(NOTIFICATION_TABLE, null, values)
            db!!.close()
            return id.toString()
        }
    }

    fun isInNotification(id: Int): Boolean {
        db = dbHandler.readableDatabase
        val qry = "Select *  from $NOTIFICATION_TABLE where $COLUMN_PLAY_ID = $id"
        val cursor = db!!.rawQuery(qry, null)
        cursor.moveToFirst()
        cursor.use { cursor ->
            return cursor.count > 0
        }
    }

    fun getNotificationList(): ArrayList<NotificationModel> {
        val list = ArrayList<NotificationModel>()
        db = dbHandler.readableDatabase
        val qry = "Select *  from $NOTIFICATION_TABLE"
        val cursor = db!!.rawQuery(qry, null)
        try {
            cursor.moveToFirst()

            for (i in 0 until cursor.count) {

                val notificationModel = NotificationModel()
                notificationModel.noti_id = cursor.getInt(cursor.getColumnIndex(COLUMN_NOTI_ID))
                notificationModel.noti_play_id =
                    cursor.getString(cursor.getColumnIndex(COLUMN_PLAY_ID))
                notificationModel.noti_title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
                notificationModel.noti_message =
                    cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE))
                notificationModel.noti_type = cursor.getString(cursor.getColumnIndex(COLUMN_TYPE))
                notificationModel.noti_ref_id =
                    cursor.getString(cursor.getColumnIndex(COLUMN_REF_ID))

                list.add(notificationModel)
                cursor.moveToNext()
            }
            return list
        } finally {
            cursor.close()
            db!!.close()
        }
    }

    fun deleteNotification(play_id: String) {
        db = dbHandler.readableDatabase
        db!!.execSQL("delete from $NOTIFICATION_TABLE where $COLUMN_PLAY_ID = '$play_id'")
        db!!.close()
    }

    fun deleteTable() {
        db = dbHandler.readableDatabase
        db!!.execSQL("delete from $NOTIFICATION_TABLE")
        db!!.close()
    }


}
package Database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    private var db: SQLiteDatabase? = null

    companion object {

        private val TAG = "DatabaseHandler"

        var DB_NAME = "SeyyarFoodCenter"
        private val DB_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        this.db = db

        val exe_product_table = ("CREATE TABLE IF NOT EXISTS " + CartData.CART_TABLE
                + "(" + CartData.COLUMN_CART_ID + " INTEGER primary key, "//primary key
                + CartData.COLUMN_PRODUCT_ID + " TEXT NOT NULL, "
                + CartData.COLUMN_PRODUCT_NAME + " TEXT, "
                + CartData.COLUMN_IN_STOCK + " TEXT, "
                + CartData.COLUMN_PRICE + " TEXT, "
                + CartData.COLUMN_QTY + " INTEGER NOT NULL, "
                + CartData.COLUMN_UNIT + " TEXT, "
                + CartData.COLUMN_UNIT_VALUE + " TEXT, "
                + CartData.COLUMN_DISCOUNT + " TEXT, "
                + CartData.COLUMN_DISCOUNT_TYPE + " TEXT, "
                + CartData.COLUMN_OFFER_ID + " TEXT, "
                + CartData.COLUMN_DISCOUNT_ID + " TEXT, "
                + CartData.COLUMN_COMBO_ID + " TEXT, "
                + CartData.COLUMN_OFFER_TYPE + " TEXT, "
                + CartData.COLUMN_OFFER_DISCOUNT + " TEXT, "
                + CartData.COLUMN_IMAGE + " TEXT, "
                + CartData.COLUMN_EXTRA_DATA + " TEXT "
                + ")")

        val exe_notification_table = ("CREATE TABLE IF NOT EXISTS " + "notification_table"
                + "(" + NotificationData.COLUMN_NOTI_ID + " INTEGER primary key, "//primary key
                + NotificationData.COLUMN_PLAY_ID + " TEXT, "
                + NotificationData.COLUMN_TITLE + " TEXT, "
                + NotificationData.COLUMN_MESSAGE + " TEXT, "
                + NotificationData.COLUMN_TYPE + " TEXT, "
                + NotificationData.COLUMN_REF_ID + " TEXT "
                + ")")

        db.execSQL(exe_product_table)
        db.execSQL(exe_notification_table)

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (newVersion == 2) {
            val exe_notification_table = ("CREATE TABLE IF NOT EXISTS " + "notification_table"
                    + "(" + NotificationData.COLUMN_NOTI_ID + " INTEGER primary key, "//primary key
                    + NotificationData.COLUMN_PLAY_ID + " TEXT, "
                    + NotificationData.COLUMN_TITLE + " TEXT, "
                    + NotificationData.COLUMN_MESSAGE + " TEXT, "
                    + NotificationData.COLUMN_TYPE + " TEXT, "
                    + NotificationData.COLUMN_REF_ID + " TEXT "
                    + ")")

            db.execSQL(exe_notification_table)
        } else if (newVersion == 3) {
            val exe_product_table1 =
                ("ALTER TABLE ${CartData.CART_TABLE} ADD COLUMN ${CartData.COLUMN_OFFER_ID} TEXT")
            val exe_product_table2 =
                ("ALTER TABLE ${CartData.CART_TABLE} ADD COLUMN ${CartData.COLUMN_DISCOUNT_ID} TEXT")
            val exe_product_table3 =
                ("ALTER TABLE ${CartData.CART_TABLE} ADD COLUMN ${CartData.COLUMN_COMBO_ID} TEXT")
            val exe_product_table4 =
                ("ALTER TABLE ${CartData.CART_TABLE} ADD COLUMN ${CartData.COLUMN_OFFER_TYPE} TEXT")
            val exe_product_table5 =
                ("ALTER TABLE ${CartData.CART_TABLE} ADD COLUMN ${CartData.COLUMN_OFFER_DISCOUNT} TEXT")

            db.execSQL(exe_product_table1)
            db.execSQL(exe_product_table2)
            db.execSQL(exe_product_table3)
            db.execSQL(exe_product_table4)
            db.execSQL(exe_product_table5)
        }
    }

}

package utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.onesignal.OSNotificationReceivedEvent
import com.onesignal.OneSignal
import com.anamoly.R
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


class NotificationServiceExtension : OneSignal.OSRemoteNotificationReceivedHandler {

    override fun remoteNotificationReceived(
        context: Context?,
        notificationReceivedEvent: OSNotificationReceivedEvent?
    ) {
        val notification = notificationReceivedEvent?.notification
        Log.d("OneSignalExample", "Notification data: " + notification?.additionalData)
        Log.d("OneSignalExample", "Notification title: " + notification?.title)
        Log.d("OneSignalExample", "Notification body: " + notification?.body)
        Log.d("OneSignalExample", "Notification big picture: " + notification?.bigPicture)
        Log.d("OneSignalExample", "Notification small icon: " + notification?.smallIcon)
        Log.d("OneSignalExample", "Notification large icon: " + notification?.largeIcon)

        val isBigStyle = (!notification?.bigPicture.isNullOrEmpty())

        val mutableNotification = notification?.mutableCopy()
        mutableNotification?.setExtender {
            if (isBigStyle) {
                val bm =
                    BitmapFactory.decodeResource(context?.resources, R.mipmap.ic_launcher)
                it.setLargeIcon(bm)
                it.setSmallIcon(R.drawable.ic_stat_notification)
                it.color = ContextCompat.getColor(context!!, R.color.colorPrimary)
                it.setStyle(
                    NotificationCompat.BigPictureStyle().bigPicture(
                        getBitmapFromURL(
                            notification.bigPicture
                        )
                    )
                )
            } else {
                val bm =
                    BitmapFactory.decodeResource(context?.resources, R.mipmap.ic_launcher)
                it.setLargeIcon(bm)
                it.setSmallIcon(R.drawable.ic_stat_notification)
                it.setColor(ContextCompat.getColor(context!!, R.color.colorPrimary))
            }
        }

        notificationReceivedEvent?.complete(mutableNotification)
        Log.d(
            "OneSignalExample",
            "Notification displayed with id3: " + notification?.androidNotificationId
        )

        val updates = Intent("NotificationUpdate")
        updates.putExtra("type", "updateNotification")
        context?.sendBroadcast(updates)
    }

    fun getBitmapFromURL(strURL: String): Bitmap? {
        try {
            val url = URL(strURL)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            return BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

}
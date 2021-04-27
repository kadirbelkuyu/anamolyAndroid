package configFCM

import Config.BaseURL
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.text.Html
import android.util.Log
import android.util.Patterns
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.anamoly.R
import com.anamoly.view.home.MainActivity
import org.json.JSONArray

import org.json.JSONException
import org.json.JSONObject
import utils.SessionManagement

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class MyFirebaseMessagingService : FirebaseMessagingService() {

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.from!!)

        // Check if message contains a data payload.
        if (remoteMessage.data.size > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)

            try {
                val jsonObject = JSONObject(remoteMessage.data as Map<*, *>)

                var messageString = ""
                var userid = -1
                var NotificationType = 11

                if (isJSONValid(jsonObject.getString("body"))) {
                    val jsonObjectBody = JSONObject(jsonObject.getString("body"))
                    messageString = jsonObjectBody.getString("Message")
                    userid = jsonObjectBody.getInt("UserId")
                    NotificationType = jsonObjectBody.getInt("NotificationType")
                } else {
                    messageString = jsonObject.getString("body")
                    userid = if (jsonObject.has("UserId")) {
                        jsonObject.getInt("UserId")
                    } else {
                        -1
                    }
                }

                var sendNotification = true
                var typeChat = false
                var chatinfoId: String = ""

                if (sendNotification) {

                    if (typeChat && chatinfoId.isNotEmpty()) {
                        val totalCount =
                            SessionManagement.CommonData.getSessionInt(this, chatinfoId)
                        SessionManagement.CommonData.setSession(this, chatinfoId, (totalCount + 1))
                    }

                    sendNotification(
                        messageString,
                        jsonObject.getString("title"),
                        if (jsonObject.has("image")) {
                            jsonObject.getString("image")
                        } else {
                            ""
                        }
                    )
                    val updates = Intent("NotificationUpdate")
                    updates.putExtra("type", "update")
                    sendBroadcast(updates)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body!!)

            sendNotification(
                remoteMessage.notification!!.body!!,
                resources.getString(R.string.app_name),
                ""
            )

            val updates = Intent("NotificationUpdate")
            updates.putExtra("type", "update")
            sendBroadcast(updates)
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    fun isJSONValid(test: String?): Boolean {
        try {
            JSONObject(test)
        } catch (ex: JSONException) { // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                JSONArray(test)
            } catch (ex1: JSONException) {
                return false
            }
        }
        return true
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d(TAG, "Refreshed token:$p0")
    }

    private fun sendNotification(message: String, title: String, imageUrl: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        if (imageUrl != null && imageUrl.length > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {
            val bitmap = getBitmapFromURL(imageUrl)

            showBigNotification(bitmap, title, message, pendingIntent)
        } else {
            groupNotification(title, message, pendingIntent)
        }

    }

    private fun simpleteNotification(title: String, message: String, pendingIntent: PendingIntent) {

        val CHANNEL_ID = "956432"// The id of the channel.

        val bm = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_notification)
            .setLargeIcon(bm)
            .setContentTitle(title)
            .setContentText(Html.fromHtml(message))
            .setAutoCancel(true)
            .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .setSound(defaultSoundUri)
            .setChannelId(CHANNEL_ID)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)// The user-visible name of the channel.
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            notificationManager.createNotificationChannel(mChannel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())

    }

    private fun showBigNotification(
        bitmap: Bitmap?,
        title: String,
        message: String,
        resultPendingIntent: PendingIntent
    ) {
        val bigPictureStyle = NotificationCompat.BigPictureStyle()
        bigPictureStyle.setBigContentTitle(title)
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString())
        bigPictureStyle.bigPicture(bitmap)

        val CHANNEL_ID = "956432"// The id of the channel.

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
        builder.setSmallIcon(R.drawable.ic_stat_notification)
        builder.setContentText(Html.fromHtml(message))
        builder.setContentTitle(title)
        builder.setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
        builder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
        builder.setAutoCancel(true)
        builder.setChannelId(CHANNEL_ID)
        builder.setContentIntent(resultPendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)// The user-visible name of the channel.
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            notificationManager.createNotificationChannel(mChannel!!)
        }

        notificationManager.notify(1, builder.build())
    }

    fun groupNotification(title: String, message: String, pendingIntent: PendingIntent) {

        val CHANNEL_ID = "983234"// The id of the channel.

        val bm = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val newMessageNotification1 = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_notification)
            .setContentTitle(title)
            .setContentText(Html.fromHtml(message))
            .setStyle(NotificationCompat.BigTextStyle().bigText(Html.fromHtml(message)))
            .setGroup(GROUP_KEY_1)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .build()

        val summaryNotification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            //set content text to support devices running API level < 24
            .setContentText(getString(R.string.app_name))
            .setSmallIcon(R.drawable.ic_stat_notification)
            .setLargeIcon(bm)
            //build summary info into InboxStyle template
            .setStyle(
                NotificationCompat.InboxStyle()
                    .setBigContentTitle("$NOTIFICATION_ID_1 new messages")
                    .setSummaryText(SessionManagement.UserData.getSession(this, BaseURL.KEY_NAME))
            )
            //specify which group this notification belongs to
            .setGroup(GROUP_KEY_1)
            //set this notification as the summary for the group
            .setGroupSummary(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)// The user-visible name of the channel.
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            notificationManager.createNotificationChannel(mChannel!!)
        }

        notificationManager.apply {
            notify(NOTIFICATION_ID_1++, newMessageNotification1)
            notify(SUMMARY_ID_1, summaryNotification)
        }
    }

    /**
     * Downloading push notification image before displaying it in
     * the notification tray
     */
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

    companion object {

        private val TAG = "MyFirebaseMsgService"
        val broadCastReceiver = "ChatBroadCast"
        val GROUP_KEY_1 = "com.reservago.simple"
        val SUMMARY_ID_1 = 0
        var NOTIFICATION_ID_1 = 1

        fun getTimeMilliSec(timeStamp: String): Long {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            try {
                val date = format.parse(timeStamp)
                return date.time
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return 0
        }
    }

}
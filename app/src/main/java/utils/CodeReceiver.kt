package utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Created on 25-03-2020.
 */
class CodeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val extras = intent.extras
        val referrerString = extras!!.getString("referrer")

        if (referrerString != null) {
            SessionManagement.PermanentData.setSession(
                context,
                "friendReferrerCode",
                referrerString
            )
        }

        Log.d("TEST", "Referrer is: $referrerString")
    }
}
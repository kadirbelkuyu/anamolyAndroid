package utils

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.view.View
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.anamoly.AppController
import com.anamoly.R

class ConnectivityReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, arg1: Intent) {
        val cm = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo

        val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting

        if (connectivityReceiverListener != null) {
            connectivityReceiverListener!!.onNetworkConnectionChanged(isConnected)
        }
    }

    interface ConnectivityReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }

    companion object {

        var connectivityReceiverListener: ConnectivityReceiverListener? = null

        val isConnected: Boolean
            get() {
                val cm = AppController().getInstance()!!.applicationContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork = cm.activeNetworkInfo
                return activeNetwork != null && activeNetwork.isConnectedOrConnecting
            }

        fun showSnackbar(_context: Context) {
            val rootView =
                (_context as Activity).window.decorView.findViewById<View>(android.R.id.content)

            val snackbar = Snackbar
                .make(rootView, "No internet connection!", Snackbar.LENGTH_LONG)

            // Changing message text color
            snackbar.setActionTextColor(Color.RED)

            // Changing action button text color
            val sbView = snackbar.view
            val textView = sbView.findViewById<View>(R.id.snackbar_text) as TextView
            textView.setTextColor(Color.RED)

            snackbar.show()
        }
    }

}
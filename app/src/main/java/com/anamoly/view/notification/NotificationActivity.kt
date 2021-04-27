package com.anamoly.view.notification

import Config.BaseURL
import Models.Notification2Model
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.response.CommonResponse
import com.anamoly.view.no_internet.NoInternetActivity
import com.vivera.notification.adapter.NotificationAdapter
import kotlinx.android.synthetic.main.activity_notification.*
import utils.ConnectivityReceiver
import utils.SessionManagement

class NotificationActivity : CommonActivity() {

    companion object {
        val TAG = NotificationActivity::class.java.simpleName
    }

    lateinit var notificationViewModel: NotificationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        setHeaderTitle(resources.getString(R.string.notifications))
        notificationViewModel = ViewModelProviders.of(this).get(NotificationViewModel::class.java)

        if (ConnectivityReceiver.isConnected) {
            makeGetNotificationList()
        } else {
            Intent(this, NoInternetActivity::class.java).apply {
                startActivityForResult(this, 9328)
            }
        }

        /*rv_notification.apply {
            layoutManager = LinearLayoutManager(this@NotificationActivity)
            adapter = NotificationAdapter(
                this@NotificationActivity,
                notificationViewModel.getNotificationList()
            )
        }*/

    }

    private fun makeGetNotificationList() {
        val params = HashMap<String, String>()
        params["user_id"] = SessionManagement.UserData.getSession(this, BaseURL.KEY_ID)

        pb_notification.visibility = View.VISIBLE
        rv_notification.visibility = View.GONE

        notificationViewModel.makeGetNotificationList(params)
            .observe(this, Observer { response: CommonResponse? ->
                pb_notification.visibility = View.GONE
                rv_notification.visibility = View.VISIBLE
                if (response != null) {
                    if (response.responce!!) {
                        val gson = Gson()
                        val type = object : TypeToken<ArrayList<Notification2Model>>() {}.type
                        val notification2ModelList =
                            gson.fromJson<ArrayList<Notification2Model>>(response.data, type)

                        rv_notification.apply {
                            layoutManager = LinearLayoutManager(this@NotificationActivity)
                            adapter = NotificationAdapter(
                                this@NotificationActivity,
                                notification2ModelList
                            )
                            CommonActivity.runLayoutAnimation(this)
                        }

                    } else {
                        CommonActivity.showToast(this, response.message!!)
                    }
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 9328 && resultCode == Activity.RESULT_OK) {
            makeGetNotificationList()
        }
    }

}

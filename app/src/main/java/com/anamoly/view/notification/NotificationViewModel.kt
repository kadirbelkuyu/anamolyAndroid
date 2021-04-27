package com.anamoly.view.notification

import Database.NotificationData
import Models.NotificationModel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.anamoly.repository.ProjectRepository
import com.anamoly.response.CommonResponse

/**
 * Created on 22-02-2020.
 */
class NotificationViewModel(application: Application) : AndroidViewModel(application) {
    val projectRepository = ProjectRepository()

    val notificationData: NotificationData

    init {
        notificationData = NotificationData(application)
    }

    fun getNotificationList(): ArrayList<NotificationModel> {
        return notificationData.getNotificationList()
    }

    fun addNotification(notificationModel: NotificationModel) {
        notificationData.setNotification(notificationModel)
    }

    fun makeGetNotificationList(params: HashMap<String, String>): LiveData<CommonResponse?> {
        return projectRepository.getNotificationList(params)
    }

}
package com.anamoly.view.user_account

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.anamoly.repository.ProjectRepository
import com.anamoly.response.CommonResponse
import okhttp3.MultipartBody

/**
 * Created on 11-02-2020.
 */
class MyProfileViewModel(application: Application) :
    AndroidViewModel(application) {
    private val projectRepository: ProjectRepository = ProjectRepository()
    var updateEmailResponseLiveData: LiveData<CommonResponse?>? = null
    var updatePhoneResponseLiveData: LiveData<CommonResponse?>? = null
    var uploadImageResponseLiveData: LiveData<CommonResponse?>? = null
    var changePasswordResponseLiveData: LiveData<CommonResponse?>? = null

    init {

    }

    fun updateEmail(params: Map<String, String>) {
        updateEmailResponseLiveData = projectRepository.updateEmail(params)
    }

    fun updatePhone(params: Map<String, String>) {
        updatePhoneResponseLiveData = projectRepository.updatePhone(params)
    }

    fun changePassword(params: Map<String, String>) {
        changePasswordResponseLiveData = projectRepository.changePassword(params)
    }

    fun uploadProfilePicture(user_id: String, image: MultipartBody.Part) {
        uploadImageResponseLiveData = projectRepository.uploadProfilePicture(user_id, image)
    }

}
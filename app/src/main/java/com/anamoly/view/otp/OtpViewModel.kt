package com.anamoly.view.otp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.anamoly.repository.ProjectRepository
import com.anamoly.response.CommonResponse
import okhttp3.MultipartBody

/**
 * Created on 11-02-2020.
 */
class OtpViewModel(application: Application) :
    AndroidViewModel(application) {
    private val projectRepository: ProjectRepository = ProjectRepository()
    var verifyEmailResponseLiveData: LiveData<CommonResponse?>? = null

    fun verifyEmail(params: Map<String, String>) {
        verifyEmailResponseLiveData = projectRepository.verifyEmail(params)
    }

    fun resendOtp(params: Map<String, String>): LiveData<CommonResponse?> {
        return projectRepository.resendOtp(params)
    }

}
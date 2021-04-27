package com.anamoly.view.forgot_password

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.anamoly.repository.ProjectRepository
import com.anamoly.response.CommonResponse


/**
 * Created on 11-02-2020.
 */
class ForgotViewModel(application: Application) :
    AndroidViewModel(application) {
    private val projectRepository: ProjectRepository = ProjectRepository()
    lateinit var forgotResponseLiveData: LiveData<CommonResponse?>

    init {

    }

    fun forgotPassword(params: Map<String, String>) {
        forgotResponseLiveData = projectRepository.forgotPassword(params)
    }

}
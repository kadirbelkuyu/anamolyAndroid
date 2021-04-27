package com.anamoly.view.checkout

import Dialogs.CommonAlertDialog
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.anamoly.repository.ProjectRepository
import com.anamoly.response.CommonResponse
import com.anamoly.response.OrderDetailResponse
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created on 03-03-2020.
 */
class CheckoutViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext

    private val projectRepository: ProjectRepository = ProjectRepository()

    val paymentStatusLiveData = MutableLiveData<Boolean>()
    val paymentOrderIDLiveData = MutableLiveData<String>()

    fun checkValidateAddress(params: HashMap<String, String>): LiveData<CommonResponse?> {
        return projectRepository.validateAddress(params)
    }

    fun getMyOrderDetail(params: HashMap<String, String>): LiveData<OrderDetailResponse?> {
        return projectRepository.getOrderDetail(params)
    }

    fun checkPaymentIntent(intent: Intent?) {
        val action: String? = intent?.action
        val data: Uri? = intent?.data
        if (data != null) {
            Log.d(context.toString(), "action::$action")
            Log.d(context.toString(), "data::${data.toString()}")

            val p: Pattern = Pattern.compile(".*anamoly://success.*")
            val m: Matcher = p.matcher(data.toString())
            if (m.matches()) {
                val paymentOrderId = data.toString().replace("anamoly://success/", "")
                Log.d(context.toString(), "dataCode::$paymentOrderId")
                paymentStatusLiveData.value = true
                paymentOrderIDLiveData.value = paymentOrderId
            } else {
                paymentStatusLiveData.value = false
            }
        }
    }

}
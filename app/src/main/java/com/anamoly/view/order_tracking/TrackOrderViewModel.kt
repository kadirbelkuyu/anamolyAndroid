package com.anamoly.view.order_tracking

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.anamoly.repository.ProjectRepository
import com.anamoly.response.OrderDetailResponse

/**
 * Created on 16-04-2020.
 */
class TrackOrderViewModel : ViewModel() {
    val projectRepository = ProjectRepository()

    fun makeGetOrderDetail(params: HashMap<String, String>): LiveData<OrderDetailResponse?> {
        return projectRepository.getOrderDetail(params)
    }

}
package com.anamoly.view.order

import Config.BaseURL
import Models.OrderModel
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.anamoly.CommonActivity
import com.anamoly.R
import com.anamoly.response.MyOrderResponse
import com.anamoly.view.order.adapter.MyOrderAdapter
import kotlinx.android.synthetic.main.activity_my_order.*
import kotlinx.android.synthetic.main.include_loader.*
import utils.ConnectivityReceiver
import utils.SessionManagement

class MyOrderActivity : CommonActivity() {

    val orderModelList = ArrayList<OrderModel>()

    lateinit var orderViewModel: OrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        orderViewModel = ViewModelProviders.of(this).get(OrderViewModel::class.java)
        setContentView(R.layout.activity_my_order)
        setHeaderTitle(resources.getString(R.string.my_orders))

        rv_my_order.layoutManager = LinearLayoutManager(this)

        if (ConnectivityReceiver.isConnected) {
            makeOrderList()
        } else {
            ConnectivityReceiver.showSnackbar(this)
        }

    }

    private fun makeOrderList() {

        include_ll.visibility = View.VISIBLE
        rv_my_order.visibility = View.GONE

        val params = HashMap<String, String>()
        params["user_id"] = SessionManagement.UserData.getSession(this, BaseURL.KEY_ID)

        orderViewModel.getMyOrderList(params)
        orderViewModel.myOrderResponseLiveData.observe(
            this,
            Observer { response: MyOrderResponse? ->
                if (response != null) {
                    include_ll.visibility = View.GONE
                    rv_my_order.visibility = View.VISIBLE

                    if (response.responce!!) {
                        orderModelList.clear()
                        orderModelList.addAll(response.orderModelList!!)

                        rv_my_order.apply {
                            adapter = MyOrderAdapter(this@MyOrderActivity, orderModelList)
                            CommonActivity.runLayoutAnimation(this)
                        }
                    } else {
                        showToast(this@MyOrderActivity, response.message!!)
                    }
                }
            })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 9843 && resultCode == Activity.RESULT_OK){
            Intent().apply {
                setResult(Activity.RESULT_OK,this)
                finish()
            }
        }
    }

}

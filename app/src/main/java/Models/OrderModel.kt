package Models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created on 08-02-2020.
 */
class OrderModel : Serializable {

    val order_id: String? = null
    val order_no: String? = null
    val order_date: String? = null
    val user_id: String? = null
    val delivery_date: String? = null
    val delivery_time: String? = null
    val delivery_time_end: String? = null
    val coupon_code: String? = null
    val discount: String? = null
    val discount_type: String? = null
    val discount_amount: String? = null
    val order_amount: String? = null
    val net_amount: String? = null
    val paid_amount: String? = null
    val paid_by: String? = null
    val payment_ref: String? = null
    val payment_log: String? = null
    val paid_date: String? = null
    val delivery_amount: String? = null
    val gateway_charges: String? = null
    val user_address_id: String? = null
    val order_note: String? = null
    val status: String? = null
    val vehicle_id: String? = null
    val delivery_boy_id: String? = null
    val created_at: String? = null
    val modified_at: String? = null
    val created_by: String? = null
    val modified_by: String? = null
    val draft: String? = null
    val total_qty: String? = null
    val house_no: String? = null
    val add_on_house_no: String? = null
    val postal_code: String? = null
    val street_name: String? = null
    val area: String? = null
    val city: String? = null
    val latitude: String? = null
    val longitude: String? = null
    val user_firstname: String? = null
    val user_lastname: String? = null
    val user_phone: String? = null
    val user_email: String? = null
    val user_company_name: String? = null
    val user_company_id: String? = null
    val vehicle_no: String? = null
    val vehicle_type: String? = null
    val driver_name: String? = null
    val driver_phone: String? = null
    val boy_name: String? = null
    val boy_phone: String? = null
    val boy_email: String? = null
    val boy_photo: String? = null

    @SerializedName("items")
    val orderItemModelList: ArrayList<OrderItemModel>? = null

}
package Models

import java.io.Serializable

/**
 * Created on 10-02-2020.
 */
class CouponModel : Serializable {

    val coupon_id: String? = null
    val coupon_code: String? = null
    val users: String? = null
    val coupon_type: String? = null
    val multi_usage: String? = null
    val discount: String? = null
    val discount_type: String? = null
    val min_order_amount: String? = null
    val max_discount_amount: String? = null
    val validity_start: String? = null
    val validity_end: String? = null
    val description_en: String? = null
    val description_ar: String? = null
    val description_nl: String? = null
    val description_tr: String? = null
    val description_de: String? = null
    val deduct_amount: String? = null

}
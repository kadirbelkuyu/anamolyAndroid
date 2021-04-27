package Models

import java.io.Serializable

/**
 * Created on 18-03-2020.
 */
class CartItemModel : Serializable {

    val cart_id: String? = null
    val user_id: String? = null
    val product_id: String? = null
    var qty: Int = 0
    val created_at: String? = null
    val modified_at: String? = null
    val created_by: String? = null
    val modified_by: String? = null
    val draft: String? = null
    val product_name_en: String? = null
    val product_name_ar: String? = null
    val product_name_nl: String? = null
    val product_name_tr: String? = null
    val product_name_de: String? = null
    val is_new: String? = null
    val in_stock: String? = null
    val price_vat_exclude: String? = null
    val vat: String? = null
    val price: String? = null
    val unit: String? = null
    val unit_ar: String? = null
    val unit_en: String? = null
    val unit_tr: String? = null
    val unit_de: String? = null
    val unit_value: String? = null
    val price_note: String? = null
    val discount: String? = null
    val discount_type: String? = null
    val product_discount_id: String? = null
    val offer_discount: String? = null
    val offer_type: String? = null
    val number_of_products: String? = null
    val product_offer_id: String? = null
    val product_image: String? = null
    val is_express: String? = null
    val effected_price: String? = null

}
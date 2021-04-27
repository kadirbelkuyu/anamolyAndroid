package Models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created on 01-02-2020.
 */
class ProductModel : Serializable {

    var product_id: String? = null
    var product_name_en: String? = null
    var product_name_ar: String? = null
    var product_name_nl: String? = null
    var product_name_tr: String? = null
    var product_name_de: String? = null
    var product_desc_en: String? = null
    var product_desc_ar: String? = null
    var product_desc_nl: String? = null
    var product_desc_tr: String? = null
    var product_desc_de: String? = null
    var product_extra_en: String? = null
    var product_extra_ar: String? = null
    var product_extra_nl: String? = null
    var product_extra_tr: String? = null
    var product_extra_de: String? = null
    var product_ingredients: String? = null
    var product_tags: String? = null
    var level: String? = null
    var is_new: String? = null
    var in_stock: String? = null
    var price: String? = null
    var qty: Int = 0
    var unit: String? = null
    var unit_ar: String? = null
    var unit_en: String? = null
    var unit_tr: String? = null
    var unit_de: String? = null
    var unit_value: String? = null
    var price_note: String? = null
    var product_barcode: String? = null
    var created_at: String? = null
    var modified_at: String? = null
    var created_by: String? = null
    var modified_by: String? = null
    var draft: String? = null
    var discount: String? = null
    var discount_type: String? = null
    var product_discount_id: String? = null
    var offer_discount: String? = null
    var offer_type: String? = null
    var number_of_products: String? = null
    var product_offer_id: String? = null
    var product_combo_id: String? = null
    var product_image: String? = null
    var group_id: String? = null
    var is_express: String? = null
    var cart_qty: Int = 0

    var product_group_id: String? = null
    var group_name_en: String? = null
    var group_name_ar: String? = null
    var group_name_nl: String? = null
    var group_name_tr: String? = null
    var group_name_de: String? = null

    var sub_category_id: String? = null
    var category_id: String? = null
    var sub_cat_name_en: String? = null
    var sub_cat_name_ar: String? = null
    var sub_cat_name_nl: String? = null
    var sub_cat_name_tr: String? = null
    var sub_cat_name_de: String? = null

    var addMargin: Boolean = false
    var slidePosition: Int = 0
    var headerPosition: Int = 0

    var qty_cart: Int = 0
    var cart_id: Int = -1

    var showQtyLoader: Boolean = false

    @SerializedName("ingredients")
    val productIngredientModelList: ArrayList<ProductIngredientModel>? = null

    @SerializedName("images")
    val productImageModelList: ArrayList<ProductImageModel>? = null

    var productComboModelList: ArrayList<ProductComboModel>? = null

    var bannerModelList: ArrayList<BannerModel>? = null

}
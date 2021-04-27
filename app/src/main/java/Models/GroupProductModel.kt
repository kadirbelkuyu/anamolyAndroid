package Models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created on 01-02-2020.
 */
class GroupProductModel : Serializable {

    val product_group_id: String? = null
    val category_id: String? = null
    val sub_category_id: String? = null
    val group_name_en: String? = null
    val group_name_ar: String? = null
    val group_name_nl: String? = null
    val group_name_tr: String? = null
    val group_name_de: String? = null
    val created_at: String? = null
    val modified_at: String? = null
    val created_by: String? = null
    val modified_by: String? = null
    val draft: String? = null
    val cat_name_en: String? = null
    val cat_name_ar: String? = null
    val cat_name_nl: String? = null
    val sub_cat_name_en: String? = null
    val sub_cat_name_ar: String? = null
    val sub_cat_name_nl: String? = null
    val sub_cat_name_tr: String? = null
    val sub_cat_name_de: String? = null

    @SerializedName("products")
    val productModelList: ArrayList<ProductModel>? = null

}
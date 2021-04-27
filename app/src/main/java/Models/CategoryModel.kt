package Models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created on 01-02-2020.
 */
class CategoryModel : Serializable {

    val category_id: String? = null
    val cat_name_en: String? = null
    val cat_name_ar: String? = null
    val cat_name_nl: String? = null
    val cat_name_tr: String? = null
    val cat_name_de: String? = null
    val cat_image: String? = null
    val cat_banner: String? = null
    val cat_sort_order: String? = null
    val created_at: String? = null
    val modified_at: String? = null
    val created_by: String? = null
    val modified_by: String? = null
    val draft: String? = null

    @SerializedName("subcategories")
    val subCategoryModelList: ArrayList<SubCategoryModel>? = null

}
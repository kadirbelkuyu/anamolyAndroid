package Models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created on 01-02-2020.
 */
class HomeProductTagModel : Serializable {

    val tag_name_en: String? = null
    val tag_name_ar: String? = null
    val tag_name_nl: String? = null
    val tag_name_tr: String? = null
    val tag_name_de: String? = null

    @SerializedName("products")
    val subCategoryModelList: ArrayList<SubCategoryModel>? = null

    @SerializedName("banners")
    val bannerModelList: ArrayList<BannerModel>? = null

}
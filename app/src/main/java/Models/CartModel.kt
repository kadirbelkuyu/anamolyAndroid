package Models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created on 18-03-2020.
 */
class CartModel : Serializable {

    @SerializedName("items")
    val cartItemModelList: ArrayList<CartItemModel>? = null

    val effected_price: Double? = null
    val items_total_price: Double? = null

}